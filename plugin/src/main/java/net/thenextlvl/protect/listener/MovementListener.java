package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.PlayerAreaEnterEvent;
import net.thenextlvl.protect.event.PlayerAreaLeaveEvent;
import net.thenextlvl.protect.event.PlayerAreaTransitionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class MovementListener implements Listener {
    private final ProtectPlugin plugin;

    @SuppressWarnings("IsCancelled")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        Stream.concat(plugin.areaProvider().getAreas(event.getFrom()), plugin.areaProvider().getAreas(event.getTo()))
                .forEach(area -> {
                    if (!area.contains(event.getFrom()) && area.contains(event.getTo())) {
                        event.setCancelled(!new PlayerAreaEnterEvent(event.getPlayer(), area).callEvent());
                    } else if (area.contains(event.getFrom()) && !area.contains(event.getTo())) {
                        event.setCancelled(!new PlayerAreaLeaveEvent(event.getPlayer(), area).callEvent());
                    }
                });
        if (event.isCancelled()) return;
        var from = plugin.areaProvider().getArea(event.getFrom());
        var to = plugin.areaProvider().getArea(event.getTo());
        if (from.equals(to)) return;
        event.setCancelled(!new PlayerAreaTransitionEvent(event.getPlayer(), from, to).callEvent());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        var movement = new PlayerMoveEvent(player, player.getLocation(), event.getVehicle().getLocation());
        onPlayerMove(movement);
        event.setCancelled(movement.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleEnter(VehicleMoveEvent event) {
        List.copyOf(event.getVehicle().getPassengers()).stream()
                .filter(entity -> entity instanceof Player)
                .forEach(entity -> {
                    var movement = new PlayerMoveEvent((Player) entity, event.getFrom(), event.getTo());
                    onPlayerMove(movement);
                    if (movement.isCancelled()) event.getVehicle().removePassenger(entity);
                });
    }
}
