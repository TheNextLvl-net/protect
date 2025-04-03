package net.thenextlvl.protect.listener;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.ProtectionFlag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class NexoListener implements Listener {
    private final ProtectPlugin plugin;
    private final ProtectionFlag<Boolean> furnitureBreak;
    private final ProtectionFlag<Boolean> furniturePlace;
    private final ProtectionFlag<Boolean> furnitureInteract;

    public NexoListener(ProtectPlugin plugin, Plugin nexo) {
        this.furnitureBreak = plugin.flagRegistry().register(nexo, "furniture_break", true, false);
        this.furniturePlace = plugin.flagRegistry().register(nexo, "furniture_place", true, false);
        this.furnitureInteract = plugin.flagRegistry().register(nexo, "furniture_interact", true, false);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurnitureBreakEvent(NexoFurnitureBreakEvent event) {
        var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, furnitureBreak, "protect.bypass.destroy"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.break");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurniturePlace(NexoFurniturePlaceEvent event) {
        var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, furniturePlace, "protect.bypass.place"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurnitureInteract(NexoFurnitureInteractEvent event) {
        var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, furnitureInteract, "protect.bypass.interact"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.interact");
    }
}
