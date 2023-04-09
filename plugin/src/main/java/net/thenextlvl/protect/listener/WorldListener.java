package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.api.area.Area;
import net.thenextlvl.protect.api.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockDamageEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.canBreak(event.getPlayer(), event.getBlock())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockPlaceEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.canPlace(event.getPlayer(), event.getBlock())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Area area = Area.highestArea(block);
        if (area.canBreak(player, block)) return;
        Protect plugin = JavaPlugin.getPlugin(Protect.class);
        Bukkit.getScheduler().runTask(plugin, () -> player.sendBlockChange(block.getLocation(), block.getBlockData()));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Area area = Area.highestArea(event.getClickedBlock());
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().isInteractable()) {
            if (!area.canUse(event.getPlayer(), event.getClickedBlock())) event.setCancelled(true);
        } else if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType().equals(Material.FARMLAND) &&
                !area.getFlag(Flag.CROP_TRAMPLE).test(event.getPlayer(), event.getInteractionPoint(), event.getMaterial()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockPhysicsEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockFromToEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockGrowEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockExplodeEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.EXPLOSIONS)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> !Area.highestArea(block).getFlag(Flag.EXPLOSIONS));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockRedstoneEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.REDSTONE)) event.setNewCurrent(0);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(PlayerBucketFillEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.canBreak(event.getPlayer(), event.getBlock())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(PlayerBucketEmptyEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.canPlace(event.getPlayer(), event.getBlock())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldEvent(BlockSpreadEvent event) {
        Area area = Area.highestArea(event.getBlock());
        if (!area.getFlag(Flag.PHYSICS)) event.setCancelled(true);
    }
}
