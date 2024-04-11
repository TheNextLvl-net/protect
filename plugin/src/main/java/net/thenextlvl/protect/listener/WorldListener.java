package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.Material;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

@RequiredArgsConstructor
public class WorldListener implements Listener {
    private final ProtectPlugin plugin;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldLoad(WorldLoadEvent event) {
        plugin.areaProvider().loadAreas(event.getWorld());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.areaProvider().saveAreas(event.getWorld());
        plugin.areaProvider().unloadAreas(event.getWorld());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(!plugin.protectionService().canBreak(
                event.getPlayer(), event.getBlock().getLocation()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(!plugin.protectionService().canBuild(
                event.getPlayer(), event.getBlock().getLocation()
        ));
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType().equals(Material.FARMLAND) && event.getAction().equals(Action.PHYSICAL))
            event.setCancelled(!plugin.protectionService().canTrample(
                    event.getPlayer(), block.getLocation()
            ));
        else if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        else if (event.getPlayer().isSneaking() && event.hasItem() && event.getMaterial().isBlock()) return;
        else if (!block.getType().isInteractable()) return;
        event.setCancelled(!plugin.protectionService().canInteract(
                event.getPlayer(), block.getLocation()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.physics));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockFading));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockBurning));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockIgniting));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMoistureChange(MoistureChangeEvent event) {
        if (!(event.getBlock().getBlockData() instanceof Farmland current)) return;
        if (!(event.getNewState().getBlockData() instanceof Farmland future)) return;
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(current.getMoisture() > future.getMoisture()
                ? plugin.flags.blockDrying : plugin.flags.blockMoisturising));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        var area = plugin.areaProvider().getArea(event.getToBlock());
        event.setCancelled(!area.getFlag(plugin.flags.physics));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockGrowth));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        event.getBlocks().removeIf(block -> {
            var area = plugin.areaProvider().getArea(block.getBlock());
            return !area.getFlag(plugin.flags.blockAbsorb);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        event.getBlocks().removeIf(block -> {
            var area = plugin.areaProvider().getArea(block.getBlock());
            return !area.getFlag(plugin.flags.blockGrowth);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            var area = plugin.areaProvider().getArea(block);
            return !area.getFlag(plugin.flags.explosions);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            var area = plugin.areaProvider().getArea(block);
            return !area.getFlag(plugin.flags.explosions);
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        if (!area.getFlag(plugin.flags.redstone)) event.setNewCurrent(0);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(!plugin.protectionService().canBreak(
                event.getPlayer(), event.getBlock().getLocation()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(!plugin.protectionService().canBuild(
                event.getPlayer(), event.getBlock().getLocation()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockSpread));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.leavesDecay));
    }
}
