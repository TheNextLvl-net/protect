package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

@RequiredArgsConstructor
public class EntityListener implements Listener {
    private final ProtectPlugin plugin;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        var area = plugin.areaProvider().getArea(event.getEntity());
        if (event.getCause().equals(EntityDamageEvent.DamageCause.KILL)) return;
        event.setCancelled(!area.getFlag(plugin.flags.damage));
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var area = plugin.areaProvider().getArea(event.getDamager());
        var flag = event.getDamager() instanceof Player && event.getEntity() instanceof Player
                ? plugin.flags.playerAttackPlayer : event.getDamager() instanceof Player
                ? plugin.flags.playerAttackEntity : event.getEntity() instanceof Player
                ? plugin.flags.entityAttackPlayer : plugin.flags.entityAttackEntity;
        event.setCancelled(!area.getFlag(flag));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        event.setCancelled(!plugin.protectionService().canInteract(event.getPlayer(), event.getRightClicked()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.entityShear));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.hangingBreak));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingPlace(HangingPlaceEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.hangingPlace));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        var area = plugin.areaProvider().getArea(event.getRightClicked());
        event.setCancelled(!area.getFlag(plugin.flags.armorStandManipulate));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(EntityDropItemEvent event) {
        var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!area.getFlag(plugin.flags.entityItemDrop));
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
    }
}
