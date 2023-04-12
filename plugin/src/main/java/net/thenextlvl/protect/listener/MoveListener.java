package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.event.AreaEnterEvent;
import net.thenextlvl.protect.event.AreaLeaveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MoveListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock() && !(event instanceof PlayerTeleportEvent)) return;
        Area.areas(event.getTo().getWorld()).forEach(area -> {
            if (!area.contains(event.getFrom()) && area.contains(event.getTo())) {
                event.setCancelled(!new AreaEnterEvent(event.getPlayer(), area).callEvent());
            } else if (area.contains(event.getFrom()) && !area.contains(event.getTo())) {
                event.setCancelled(!new AreaLeaveEvent(event.getPlayer(), area).callEvent());
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }
}
