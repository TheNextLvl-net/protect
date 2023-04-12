package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.event.AreaEnterEvent;
import net.thenextlvl.protect.event.AreaLeaveEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AreaListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAreaEnter(AreaEnterEvent event) {
        if (!event.getArea().getFlag(Flag.ENTER).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAreaLeave(AreaLeaveEvent event) {
        if (!event.getArea().getFlag(Flag.LEAVE).test(event.getPlayer())) event.setCancelled(true);
    }
}
