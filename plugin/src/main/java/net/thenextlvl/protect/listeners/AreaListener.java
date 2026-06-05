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
import org.jspecify.annotations.Nullable;

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
        final var message = plugin.flags.greetings.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null);
        if (message != null) event.getPlayer().sendMessage(deserialize(message, event));
        final var actionbar = plugin.flags.greetingsActionbar.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null);
        if (actionbar != null) event.getPlayer().sendActionBar(deserialize(actionbar, event));
        final var title = parseTitle(plugin.flags.greetingsTitle.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null), event);
        if (title != null) event.getPlayer().showTitle(title);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void farewell(final PlayerAreaLeaveEvent event) {
        final var message = plugin.flags.farewell.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null);
        if (message != null) event.getPlayer().sendMessage(deserialize(message, event));
        final var actionbar = plugin.flags.farewellActionbar.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null);
        if (actionbar != null) event.getPlayer().sendActionBar(deserialize(actionbar, event));
        final var title = parseTitle(plugin.flags.farewellTitle.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null), event);
        if (title != null) event.getPlayer().showTitle(title);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAreaTransition(final PlayerAreaTransitionEvent event) {
        final var collides = plugin.flags.collisions.require(event.getArea()).value();
        plugin.collisionController().setCollidable(event.getPlayer(), collides);

        final var weather = plugin.flags.weather.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null);
        if (weather != null) event.getPlayer().setPlayerWeather(weather);
        else if (event.getPrevious().hasFlag(plugin.flags.weather))
            event.getPlayer().resetPlayerWeather();
        final var time = plugin.flags.time.getValue(event.getArea()).map(net.thenextlvl.protect.flag.ValueFlag::value).orElse(null);
        if (time != null) event.getPlayer().setPlayerTime(time, false);
        else if (event.getPrevious().hasFlag(plugin.flags.time))
            event.getPlayer().resetPlayerTime();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAreaFlagChange(final AreaFlagChangeEvent<?> event) {
        if (event.getFlag().equals(plugin.flags.weather)) {
            final var weather = (WeatherType) event.getNewState().orElse(null);
            event.getArea().getHighestPlayers().forEach(player -> {
                if (weather != null) player.setPlayerWeather(weather);
                else player.resetPlayerWeather();
            });
        } else if (event.getFlag().equals(plugin.flags.time)) {
            final var time = (Long) event.getNewState().orElse(null);
            event.getArea().getHighestPlayers().forEach(player -> {
                if (time != null) player.setPlayerTime(time, false);
                else player.resetPlayerTime();
            });
        } else if (event.getFlag().equals(plugin.flags.collisions)) {
            final var collides = (boolean) event.getNewState().orElse(null);
            event.getArea().getHighestPlayers().forEach(player -> {
                plugin.collisionController().setCollidable(player, collides);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAreaFlagReset(final AreaFlagResetEvent event) {
        if (event.getFlag().equals(plugin.flags.weather)) {
            event.getArea().getHighestPlayers().forEach(Player::resetPlayerWeather);
        } else if (event.getFlag().equals(plugin.flags.time))
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

    private @Nullable Title parseTitle(@Nullable final String text, final PlayerAreaEvent event) {
        if (text == null) return null;
        final var split = text.split("\\\\n|<newline>|<br>", 2);
        final var title = deserialize(split[0], event);
        final var subtitle = split.length == 2 ? deserialize(split[1], event) : Component.empty();
        return Title.title(title, subtitle);
    }
}
