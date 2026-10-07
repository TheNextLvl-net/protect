package net.thenextlvl.protect.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.flag.AreaFlagChangeEvent;
import net.thenextlvl.protect.area.event.flag.AreaFlagResetEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaEnterEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaTransitionEvent;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class AreaListener implements Listener {
    private final ProtectPlugin plugin;

    public AreaListener(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAreaEnter(final PlayerAreaEnterEvent event) {
        plugin.failed(event.getPlayer(), event, event.getArea(), "area.failed.enter");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAreaLeave(final PlayerAreaLeaveEvent event) {
        plugin.failed(event.getPlayer(), event, event.getArea(), "area.failed.leave");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void greetings(final PlayerAreaEnterEvent event) {
        final var area = event.getArea();
        area.findFlagValue(plugin.flags.greetings).ifPresent(message ->
                event.getPlayer().sendMessage(deserialize(message, event)));
        area.findFlagValue(plugin.flags.greetingsActionbar).ifPresent(actionbar ->
                event.getPlayer().sendActionBar(deserialize(actionbar, event)));
        area.findFlagValue(plugin.flags.greetingsTitle).ifPresent(title ->
                event.getPlayer().showTitle(parseTitle(title, event)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void farewell(final PlayerAreaLeaveEvent event) {
        final var area = event.getArea();
        area.findFlagValue(plugin.flags.farewell).ifPresent(message ->
                event.getPlayer().sendMessage(deserialize(message, event)));
        area.findFlagValue(plugin.flags.farewellActionbar).ifPresent(actionbar ->
                event.getPlayer().sendActionBar(deserialize(actionbar, event)));
        area.findFlagValue(plugin.flags.farewellTitle).ifPresent(title ->
                event.getPlayer().showTitle(parseTitle(title, event)));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAreaTransition(final PlayerAreaTransitionEvent event) {
        final var collides = event.getArea().getFlagValue(plugin.flags.collisions);
        plugin.collisionController().setCollidable(event.getPlayer(), collides);

        final var weather = event.getArea().findFlagValue(plugin.flags.weather);
        if (weather.isPresent()) event.getPlayer().setPlayerWeather(weather.get());
        else if (event.getPrevious().hasFlag(plugin.flags.weather))
            event.getPlayer().resetPlayerWeather();
        final var time = event.getArea().findFlagValue(plugin.flags.time);
        if (time.isPresent()) event.getPlayer().setPlayerTime(time.get(), false);
        else if (event.getPrevious().hasFlag(plugin.flags.time))
            event.getPlayer().resetPlayerTime();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAreaFlagChange(final AreaFlagChangeEvent event) {
        if (event.getFlagInstance().equals(plugin.flags.weather)) {
            final var weather = event.<WeatherType>getFlag().value();
            event.getArea().getHighestPlayers().forEach(player -> player.setPlayerWeather(weather));
        } else if (event.getFlagInstance().equals(plugin.flags.time)) {
            final var time = event.<Long>getFlag().value();
            event.getArea().getHighestPlayers().forEach(player -> player.setPlayerTime(time, false));
        } else if (event.getFlagInstance().equals(plugin.flags.collisions)) {
            final var collides = event.<Boolean>getFlag().value();
            event.getArea().getHighestPlayers().forEach(player -> {
                plugin.collisionController().setCollidable(player, collides);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAreaFlagReset(final AreaFlagResetEvent event) {
        if (event.getFlagInstance().equals(plugin.flags.weather))
            event.getArea().getHighestPlayers().forEach(Player::resetPlayerWeather);
        else if (event.getFlagInstance().equals(plugin.flags.time))
            event.getArea().getHighestPlayers().forEach(Player::resetPlayerTime);
    }

    private static final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.resolver(TagResolver.standard()))
            .build();

    private Component deserialize(final String text, final PlayerAreaEvent event) {
        return miniMessage.deserialize(text,
                Placeholder.component("player", event.getPlayer().name()),
                Placeholder.parsed("area", event.getArea().getName()));
    }

    private Title parseTitle(final String text, final PlayerAreaEvent event) {
        final var split = text.split("\\\\n|<newline>|<br>", 2);
        final var title = deserialize(split[0], event);
        final var subtitle = split.length == 2 ? deserialize(split[1], event) : Component.empty();
        return Title.title(title, subtitle);
    }
}
