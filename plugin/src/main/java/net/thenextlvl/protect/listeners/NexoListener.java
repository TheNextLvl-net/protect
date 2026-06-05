package net.thenextlvl.protect.listeners;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.FlagHolder;
import net.thenextlvl.protect.flag.ProtectionFlag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public final class NexoListener implements Listener {
    private final ProtectPlugin plugin;
    private final FlagHolder<ProtectionFlag> furnitureBreak;
    private final FlagHolder<ProtectionFlag> furniturePlace;
    private final FlagHolder<ProtectionFlag> furnitureInteract;

    public NexoListener(final ProtectPlugin plugin, final Plugin nexo) {
        this.furnitureBreak = plugin.flagRegistry().register(nexo, ProtectionFlag.create(key(nexo, "furniture_break"), true, false));
        this.furniturePlace = plugin.flagRegistry().register(nexo, ProtectionFlag.create(key(nexo, "furniture_place"), true, false));
        this.furnitureInteract = plugin.flagRegistry().register(nexo, ProtectionFlag.create(key(nexo, "furniture_interact"), true, false));
        this.plugin = plugin;
    }

    @SuppressWarnings("PatternValidation")
    private static Key key(final Plugin plugin, final String name) {
        return Key.key(plugin.getName().replace("-", "_").toLowerCase(Locale.ROOT), name);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurnitureBreakEvent(final NexoFurnitureBreakEvent event) {
        final var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, furnitureBreak, "protect.bypass.destroy"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.break");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurniturePlace(final NexoFurniturePlaceEvent event) {
        final var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, furniturePlace, "protect.bypass.place"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onNexoFurnitureInteract(final NexoFurnitureInteractEvent event) {
        final var area = plugin.areaProvider().getArea(event.getBaseEntity());
        event.setCancelled(!plugin.protectionService().canPerformAction(event.getPlayer(), area, furnitureInteract, "protect.bypass.interact"));
        plugin.failed(event.getPlayer(), event, area, "area.failed.interact");
    }
}
