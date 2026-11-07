package net.thenextlvl.protect.listeners;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class NexoListener implements Listener {
    private final ProtectPlugin plugin;

    public NexoListener(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurnitureBreakEvent(final NexoFurnitureBreakEvent event) {
        final var destroy = plugin.flags.nexoFurnitureBreak;
        final var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, destroy, "protect.bypass.destroy"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.break");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurniturePlace(final NexoFurniturePlaceEvent event) {
        final var place = plugin.flags.nexoFurniturePlace;
        final var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, place, "protect.bypass.place"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurnitureInteract(final NexoFurnitureInteractEvent event) {
        final var interact = plugin.flags.nexoFurnitureInteract;
        final var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, interact, "protect.bypass.interact"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.interact");
    }
}
