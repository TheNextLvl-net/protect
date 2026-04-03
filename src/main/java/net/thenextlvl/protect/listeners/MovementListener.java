package net.thenextlvl.protect.listeners;

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

public final class MovementListener implements Listener {
    private final ProtectPlugin plugin;

    public MovementListener(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        if (canMove(event.getPlayer(), event.getFrom(), event.getTo())) return;
        event.getFrom().setPitch(event.getTo().getPitch());
        event.getFrom().setYaw(event.getTo().getYaw());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityMove(final EntityMoveEvent event) {
        if (!event.hasChangedBlock()) return;
        if (canMove(event.getEntity(), event.getFrom(), event.getTo())) return;
        event.getFrom().setPitch(event.getTo().getPitch());
        event.getFrom().setYaw(event.getTo().getYaw());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof final Player player)) return;
        if (canMove(player, player.getLocation(), event.getVehicle().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleMove(final VehicleMoveEvent event) {
        if (canMove(event.getVehicle(), event.getFrom(), event.getTo())) return;
        event.getVehicle().teleportAsync(event.getFrom());
    }

    private boolean canMove(final Entity entity, final Location from, final Location to) {
        return plugin.areaProvider().getAreas(from).allMatch(area -> {
            return area.contains(to) || canLeave(entity, from, to, area);
        }) && plugin.areaProvider().getAreas(to).allMatch(area -> {
            return area.contains(from) || canEnter(entity, from, to, area);
        });
    }

    private boolean canEnter(final Entity entity, final Location from, final Location to, final Area area) {
        final var enter = plugin.protectionService().canEnter(entity, area);
        if (!(entity instanceof final Player player)) return enter;
        final var event = new PlayerAreaEnterEvent(player, area);
        event.setCancelled(!canTransition(player, from, to, !enter));
        return event.callEvent();
    }

    private boolean canLeave(final Entity entity, final Location from, final Location to, final Area area) {
        final var leave = plugin.protectionService().canLeave(entity, area);
        if (!(entity instanceof final Player player)) return leave;
        final var event = new PlayerAreaLeaveEvent(player, area);
        event.setCancelled(!canTransition(player, from, to, !leave));
        return event.callEvent();
    }

    private boolean canTransition(final Player player, final Location from, final Location to, final boolean cancel) {
        final var areaFrom = plugin.areaProvider().getArea(from);
        final var areaTo = plugin.areaProvider().getArea(to);
        if (areaFrom.equals(areaTo)) return true;

        final var event = new PlayerAreaTransitionEvent(player, areaFrom, areaTo);
        event.setCancelled(cancel);
        return event.callEvent();
    }
}
