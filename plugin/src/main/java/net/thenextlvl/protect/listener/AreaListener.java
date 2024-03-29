package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.AreaEnterEvent;
import net.thenextlvl.protect.event.AreaLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class AreaListener implements Listener {
    private final ProtectPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAreaEnter(AreaEnterEvent event) {
        event.setCancelled(!plugin.protectionService().canEnter(event.getPlayer(), event.getArea()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAreaLeave(AreaLeaveEvent event) {
        event.setCancelled(!plugin.protectionService().canLeave(event.getPlayer(), event.getArea()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void greetings(AreaEnterEvent event) {
        var message = event.getArea().getFlag(plugin.flags.greetings);
        if (message != null) event.getPlayer().sendRichMessage(message);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void farewell(AreaLeaveEvent event) {
        var message = event.getArea().getFlag(plugin.flags.farewell);
        if (message != null) event.getPlayer().sendRichMessage(message);
    }
}
