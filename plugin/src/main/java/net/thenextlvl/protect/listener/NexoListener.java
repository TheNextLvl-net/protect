package net.thenextlvl.protect.listener;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.standard.BooleanFlag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class NexoListener implements Listener {
    private final BooleanFlag furnitureBreak = new BooleanFlag(Key.key("nexo", "furniture_break"), true, false);
    private final BooleanFlag furniturePlace = new BooleanFlag(Key.key("furniture_place"), true, false);
    private final BooleanFlag furnitureInteract = new BooleanFlag(Key.key("furniture_interact"), true, false);

    private final ProtectPlugin plugin;

    public NexoListener(ProtectPlugin plugin, Plugin nexo) {
        plugin.flagRegistry().register(nexo, this.furnitureBreak);
        plugin.flagRegistry().register(nexo, this.furniturePlace);
        plugin.flagRegistry().register(nexo, this.furnitureInteract);
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
