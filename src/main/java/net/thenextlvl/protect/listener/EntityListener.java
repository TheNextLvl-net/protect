package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public final class EntityListener implements Listener {
    private final ProtectPlugin plugin;

    public EntityListener(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var area = plugin.areaProvider().getArea(player);
        if (event.getCause().equals(EntityDamageEvent.DamageCause.KILL)) return;
        event.setCancelled(!area.getFlag(plugin.flags.damage));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() == null) return;
        event.setCancelled(!plugin.protectionService().canAttack(event.getAttacker(), event.getVehicle()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) return;
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.naturalEntitySpawn));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(event.getItem() == null && !area.getFlag(plugin.flags.hunger));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() == null) return;
        event.setCancelled(!plugin.protectionService().canAttack(event.getEntity(), event.getTarget()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canAttack(event.getDamager(), event.getEntity()));
        plugin.failed(event.getDamager(), event, area, "area.failed.attack");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        event.setCancelled(!plugin.protectionService().canInteract(event.getPlayer(), event.getRightClicked()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getRightClicked()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        event.setCancelled(!plugin.protectionService().canShear(event.getPlayer(), event.getEntity()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getEntity()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canAttack(event.getRemover(), event.getEntity()));
        plugin.failed(event.getRemover(), area, "area.failed.attack");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canPlace(event.getPlayer(), area));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPlace(EntityPlaceEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canPlace(event.getPlayer(), area));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(!plugin.protectionService().canInteract(event.getPlayer(), event.getRightClicked()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getRightClicked()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(EntityDropItemEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.entityItemDrop));
        plugin.failed(event.getEntity(), event, area, "area.failed.drop");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        var area = plugin.areaProvider().getArea(event.getPlayer());
        event.setCancelled(!area.getFlag(plugin.flags.playerItemDrop));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        var area = plugin.areaProvider().getArea(event.getItem());
        event.setCancelled(!area.getFlag(plugin.flags.entityItemPickup));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.shoot));
        plugin.failed(event.getEntity(), event, area, "area.failed.shoot");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityBreakDoor(EntityBreakDoorEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.entityBreakDoor));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSheepEatGrass(EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.SHEEP)) return;
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.sheepEatGrass));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.FALLING_BLOCK)) return;
        if (event.getTo().isAir()) return;
        var origin = event.getEntity().getOrigin();
        var from = origin != null ? plugin.areaProvider().getArea(origin) : null;
        var area = plugin.areaProvider().getArea(event.getEntity());
        if (from != null && from.canInteract(area)) return;
        event.setCancelled(true);
    }
}
