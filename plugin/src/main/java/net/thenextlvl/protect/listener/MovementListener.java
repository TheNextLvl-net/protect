package net.thenextlvl.protect.listener;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.player.PlayerAreaEnterEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.area.event.player.PlayerAreaTransitionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.stream.Stream;

public class MovementListener implements Listener {
    private final ProtectPlugin plugin;

    public MovementListener(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        if (canMove(event.getPlayer(), event.getFrom(), event.getTo())) return;
        event.getFrom().setPitch(event.getTo().getPitch());
        event.getFrom().setYaw(event.getTo().getYaw());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityMove(EntityMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        if (canMove(event.getEntity(), event.getFrom(), event.getTo())) return;
        event.getFrom().setPitch(event.getTo().getPitch());
        event.getFrom().setYaw(event.getTo().getYaw());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (canMove(player, player.getLocation(), event.getVehicle().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleMove(VehicleMoveEvent event) {
        if (canMove(event.getVehicle(), event.getFrom(), event.getTo())) return;
        event.getVehicle().teleportAsync(event.getFrom());
    }

    private boolean canMove(Entity entity, Location from, Location to) {
        return Stream.concat(
                plugin.areaProvider().getAreas(from),
                plugin.areaProvider().getAreas(to)
        ).distinct().allMatch(area -> {
            if (!area.contains(from) && area.contains(to)) return canEnter(entity, from, to, area);
            if (area.contains(from) && !area.contains(to)) return canLeave(entity, from, to, area);
            return true;
        });

    }

    private boolean canEnter(Entity entity, Location from, Location to, Area area) {
        return (entity instanceof Player player
                && new PlayerAreaEnterEvent(player, area).callEvent()
                && canTransition(player, from, to))
               || plugin.protectionService().canEnter(entity, area);
    }

    private boolean canLeave(Entity entity, Location from, Location to, Area area) {
        return (entity instanceof Player player
                && new PlayerAreaLeaveEvent(player, area).callEvent()
                && canTransition(player, from, to))
               || plugin.protectionService().canLeave(entity, area);
    }

    private boolean canTransition(Player player, Location from, Location to) {
        var areaFrom = plugin.areaProvider().getArea(from);
        var areaTo = plugin.areaProvider().getArea(to);

        return areaFrom.equals(areaTo) || new PlayerAreaTransitionEvent(player, areaFrom, areaTo).callEvent();
    }
}
