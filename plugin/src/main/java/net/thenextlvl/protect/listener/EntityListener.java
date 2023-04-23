package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Area area = Area.highestArea(event.getEntity());
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;
        if (area.getFlag(Flag.DAMAGE)) return;
        event.getEntity().setFireTicks(0);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Area area = Area.highestArea(event.getEntity());
        if (!area.getFlag(Flag.ENTITY_SPAWN)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Area area = Area.highestArea(event.getEntity());
        if (event.getItem() == null && !area.getFlag(Flag.HUNGER)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Area area = Area.highestArea(event.getDamager());
        var flag = event.getDamager() instanceof Player && event.getEntity() instanceof Player ? Flag.ATTACK_PLAYERS : Flag.ATTACK_ENTITIES;
        if (!area.getFlag(flag)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        Area area = Area.highestArea(event.getRightClicked());
        if (!area.getFlag(Flag.ENTITY_INTERACT).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        Area area = Area.highestArea(event.getEntity());
        if (!area.getFlag(Flag.ENTITY_INTERACT).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        Area area = Area.highestArea(event.getEntity());
        if (event.getRemover() == null || area.getFlag(Flag.HANGING_BREAK).test(event.getRemover())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        Area area = Area.highestArea(event.getEntity());
        if (event.getPlayer() == null || area.getFlag(Flag.HANGING_PLACE).test(event.getPlayer())) return;
        event.getPlayer().updateInventory();
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Area area = Area.highestArea(event.getRightClicked());
        if (!area.getFlag(Flag.ARMOR_STAND_MANIPULATE).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Area area = Area.highestArea(event.getItemDrop());
        if (!area.getFlag(Flag.ITEM_DROP).test(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Area area = Area.highestArea(event.getItem());
        if (!area.getFlag(Flag.ITEM_DROP).test(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Area area = Area.highestArea(event.getEntity());
        if (!area.getFlag(Flag.SHOOT)) event.setCancelled(true);
    }
}
