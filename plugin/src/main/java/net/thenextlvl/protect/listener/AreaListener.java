package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.event.flag.AreaFlagChangeEvent;
import net.thenextlvl.protect.area.event.flag.AreaFlagResetEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaEnterEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaTransitionEvent;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

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
        if (message != null) event.getPlayer().sendRichMessage(message);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void farewell(PlayerAreaLeaveEvent event) {
        var message = event.getArea().getFlag(plugin.flags.farewell);
        if (message != null) event.getPlayer().sendRichMessage(message);
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
}
