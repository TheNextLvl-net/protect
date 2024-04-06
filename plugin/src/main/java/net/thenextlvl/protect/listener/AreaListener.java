package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.PlayerAreaEnterEvent;
import net.thenextlvl.protect.event.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.event.PlayerAreaTransitionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class AreaListener implements Listener {
    private final ProtectPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAreaEnter(PlayerAreaEnterEvent event) {
        event.setCancelled(!plugin.protectionService().canEnter(event.getPlayer(), event.getArea()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAreaLeave(PlayerAreaLeaveEvent event) {
        event.setCancelled(!plugin.protectionService().canLeave(event.getPlayer(), event.getArea()));
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
}
