package net.thenextlvl.protect.listeners;

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
import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;
import org.bukkit.entity.Entity;

public final class EntityListener implements Listener {
    private final ProtectPlugin plugin;

    public EntityListener(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof final Player player)) return;
        final var area = plugin.areaProvider().getArea(player);
        if (event.getCause().equals(EntityDamageEvent.DamageCause.KILL)) return;
        event.setCancelled(!area.getFlag(plugin.flags.damage));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onVehicleDestroy(final VehicleDestroyEvent event) {
        if (event.getAttacker() == null) return;
        event.setCancelled(!plugin.protectionService().canAttack(event.getAttacker(), event.getVehicle()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(final EntitySpawnEvent event) {
        if (!event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)) return;
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.naturalEntitySpawn));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(event.getItem() == null && !area.getFlag(plugin.flags.hunger));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityTarget(final EntityTargetEvent event) {
        if (event.getTarget() == null) return;
        event.setCancelled(!plugin.protectionService().canAttack(event.getEntity(), event.getTarget()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canAttack(event.getDamager(), event.getEntity()));
        plugin.failed(event.getDamager(), event, area, "area.failed.attack");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityKnockback(final EntityKnockbackEvent event) {
        Entity source = null;
        if (event instanceof final EntityKnockbackByEntityEvent byEntity) {
            source = byEntity.getHitBy();
        } else if (event instanceof final EntityPushedByEntityAttackEvent pushedBy) {
            source = pushedBy.getPushedBy();
        }
        event.setCancelled(!plugin.protectionService().canKnockback(source, event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEntityEvent event) {
        event.setCancelled(!plugin.protectionService().canInteract(event.getPlayer(), event.getRightClicked()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getRightClicked()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerShearEntity(final PlayerShearEntityEvent event) {
        event.setCancelled(!plugin.protectionService().canShear(event.getPlayer(), event.getEntity()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getEntity()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(final HangingBreakByEntityEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canAttack(event.getRemover(), event.getEntity()));
        plugin.failed(event.getRemover(), area, "area.failed.attack");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingPlace(final HangingPlaceEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canPlace(event.getPlayer(), area));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityPlace(final EntityPlaceEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canPlace(event.getPlayer(), area));
        plugin.failed(event.getPlayer(), event, area, "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmorStandManipulate(final PlayerArmorStandManipulateEvent event) {
        event.setCancelled(!plugin.protectionService().canInteract(event.getPlayer(), event.getRightClicked()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getRightClicked()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(final EntityDropItemEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.entityItemDrop));
        plugin.failed(event.getEntity(), event, area, "area.failed.drop");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent event) {
        final var area = plugin.areaProvider().getArea(event.getPlayer());
        event.setCancelled(!area.getFlag(plugin.flags.playerItemDrop));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(final EntityPickupItemEvent event) {
        final var area = plugin.areaProvider().getArea(event.getItem());
        event.setCancelled(!area.getFlag(plugin.flags.entityItemPickup));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.shoot));
        plugin.failed(event.getEntity(), event, area, "area.failed.shoot");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityBreakDoor(final EntityBreakDoorEvent event) {
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.entityBreakDoor));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSheepEatGrass(final EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.SHEEP)) return;
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.sheepEatGrass));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.FALLING_BLOCK)) return;
        if (event.getTo().isAir()) return;
        final var origin = event.getEntity().getOrigin();
        final var from = origin != null ? plugin.areaProvider().getArea(origin) : null;
        final var area = plugin.areaProvider().getArea(event.getEntity());
        if (from != null && from.canInteract(area)) return;
        event.setCancelled(true);
    }
}
