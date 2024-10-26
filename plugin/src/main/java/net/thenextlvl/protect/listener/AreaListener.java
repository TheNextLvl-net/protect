package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
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
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class AreaListener implements Listener {
    private final ProtectPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAreaEnter(PlayerAreaEnterEvent event) {
        if (plugin.protectionService().canEnter(event.getPlayer(), event.getArea())) return;
        plugin.failed(event.getPlayer(), event.getArea(), "area.failed.enter");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAreaLeave(PlayerAreaLeaveEvent event) {
        if (plugin.protectionService().canLeave(event.getPlayer(), event.getArea())) return;
        plugin.failed(event.getPlayer(), event.getArea(), "area.failed.leave");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void greetings(PlayerAreaEnterEvent event) {
        var message = event.getArea().getFlag(plugin.flags.greetings);
        if (message != null) event.getPlayer().sendMessage(deserialize(message, event));
        var actionbar = event.getArea().getFlag(plugin.flags.greetingsActionbar);
        if (actionbar != null) event.getPlayer().sendActionBar(deserialize(actionbar, event));
        var title = parseTitle(event.getArea().getFlag(plugin.flags.greetingsTitle), event);
        if (title != null) event.getPlayer().showTitle(title);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void farewell(PlayerAreaLeaveEvent event) {
        var message = event.getArea().getFlag(plugin.flags.farewell);
        if (message != null) event.getPlayer().sendMessage(deserialize(message, event));
        var actionbar = event.getArea().getFlag(plugin.flags.farewellActionbar);
        if (actionbar != null) event.getPlayer().sendActionBar(deserialize(actionbar, event));
        var title = parseTitle(event.getArea().getFlag(plugin.flags.farewellTitle), event);
        if (title != null) event.getPlayer().showTitle(title);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAreaTransition(PlayerAreaTransitionEvent event) {
        var weather = event.getArea().getFlag(plugin.flags.weather);
        if (weather != null) event.getPlayer().setPlayerWeather(weather);
        else if (event.getPrevious().hasFlag(plugin.flags.weather))
            event.getPlayer().resetPlayerWeather();
        var time = event.getArea().getFlag(plugin.flags.time);
        if (time != null) event.getPlayer().setPlayerTime(time, false);
        else if (event.getPrevious().hasFlag(plugin.flags.time))
            event.getPlayer().resetPlayerTime();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAreaFlagChange(AreaFlagChangeEvent<?> event) {
        if (event.getFlag().equals(plugin.flags.weather)) {
            var weather = (WeatherType) event.getNewState();
            event.getArea().getHighestPlayers().forEach(player -> {
                if (weather != null) player.setPlayerWeather(weather);
                else player.resetPlayerWeather();
            });
        } else if (event.getFlag().equals(plugin.flags.time)) {
            var time = (Long) event.getNewState();
            event.getArea().getHighestPlayers().forEach(player -> {
                if (time != null) player.setPlayerTime(time, false);
                else player.resetPlayerTime();
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAreaFlagReset(AreaFlagResetEvent<?> event) {
        if (event.getFlag().equals(plugin.flags.weather)) {
            event.getArea().getHighestPlayers().forEach(Player::resetPlayerWeather);
        } else if (event.getFlag().equals(plugin.flags.time))
            event.getArea().getHighestPlayers().forEach(Player::resetPlayerTime);
    }

    private static final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.resolver(TagResolver.standard()))
            .build();

    private Component deserialize(String text, PlayerAreaEvent event) {
        return miniMessage.deserialize(text,
                Placeholder.component("player", event.getPlayer().name()),
                Placeholder.parsed("area", event.getArea().getName()));
    }

    private @Nullable Title parseTitle(@Nullable String text, PlayerAreaEvent event) {
        if (text == null) return null;
        var split = text.split("\\\\n|<newline>|<br>", 2);
        var title = deserialize(split[0], event);
        var subtitle = split.length == 2 ? deserialize(split[1], event) : Component.empty();
        return Title.title(title, subtitle);
    }
}
