package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        plugin.areaProvider().loadAreas(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCropTrample(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (event.getClickedBlock() == null) return;
        if (!event.getClickedBlock().getType().equals(Material.FARMLAND)) return;
        event.setCancelled(!plugin.protectionService().canTrample(
                event.getPlayer(), event.getClickedBlock().getLocation()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getClickedBlock() == null) return;
        if (event.getPlayer().isSneaking() && event.hasItem() && event.getMaterial().isBlock()) return;
        if (!BlockUtil.isInteractable(event.getClickedBlock().getType(), event.getMaterial())) return;
        event.setCancelled(!plugin.protectionService().canInteract(
                event.getPlayer(), event.getClickedBlock().getLocation()
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
        event.setCancelled(!plugin.protectionService().canFillBucket(
                event.getPlayer(), event.getBlock().getLocation()
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(!plugin.protectionService().canEmptyBucket(
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerChangeCauldronLevel(CauldronLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        event.setCancelled(!switch (event.getReason()) {
            case ARMOR_WASH -> plugin.protectionService().canWashArmor(player, event.getBlock().getLocation());
            case BANNER_WASH -> plugin.protectionService().canWashBanner(player, event.getBlock().getLocation());
            case BOTTLE_EMPTY -> plugin.protectionService().canEmptyBottle(player, event.getBlock().getLocation());
            case BOTTLE_FILL -> plugin.protectionService().canFillBottle(player, event.getBlock().getLocation());
            case BUCKET_EMPTY -> plugin.protectionService().canEmptyBucket(player, event.getBlock().getLocation());
            case BUCKET_FILL -> plugin.protectionService().canFillBucket(player, event.getBlock().getLocation());
            case SHULKER_WASH -> plugin.protectionService().canWashShulker(player, event.getBlock().getLocation());
            default -> true;
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        var flag = switch (event.getReason()) {
            case EXTINGUISH -> plugin.flags.cauldronExtinguishEntity;
            case EVAPORATE -> plugin.flags.cauldronEvaporation;
            case NATURAL_FILL -> plugin.flags.naturalCauldronFill;
            case UNKNOWN -> plugin.flags.cauldronLevelChangeUnknown;
            default -> null;
        };
        if (flag != null) event.setCancelled(!area.getFlag(flag));
    }
}
