package net.thenextlvl.protect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

@RequiredArgsConstructor
public class WorldListener implements Listener {
    private final ProtectPlugin plugin;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldInit(WorldInitEvent event) {
        plugin.areaProvider().load(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.areaProvider().save(event.getWorld());
        plugin.areaProvider().unload(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldSave(WorldSaveEvent event) {
        plugin.areaProvider().save(event.getWorld());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(!plugin.protectionService().canDestroy(event.getPlayer(), event.getBlock().getLocation()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getBlock()), "area.failed.break");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(!plugin.protectionService().canPlace(event.getPlayer(), event.getBlock().getLocation()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getBlock()), "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPhysicalInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) return;
        if (event.getClickedBlock() == null) return;
        var location = event.getClickedBlock().getLocation();
        event.setCancelled(event.getClickedBlock().getType().equals(Material.FARMLAND)
                ? !plugin.protectionService().canTrample(event.getPlayer(), location)
                : !plugin.protectionService().canInteractPhysical(event.getPlayer(), location));
        if (!event.getClickedBlock().getType().equals(Material.FARMLAND)) return;
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(location), "area.failed.break");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getClickedBlock() == null) return;
        if (event.getPlayer().isSneaking() && event.hasItem() && event.getMaterial().isBlock()) return;
        if (!BlockUtil.isInteractable(event.getClickedBlock().getType(), event.getMaterial())) return;
        event.setCancelled(!plugin.protectionService().canInteract(event.getPlayer(), event.getClickedBlock().getLocation()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getClickedBlock()), "area.failed.interact");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        event.setCancelled(!event.getReplacedBlockStates().stream().allMatch(blockState ->
                plugin.protectionService().canPlace(event.getPlayer(), blockState.getLocation())));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getBlock()), "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockReceiveGameEvent(BlockReceiveGameEvent event) {
        event.setCancelled(isInteractionRestricted(
                event.getBlock().getLocation(),
                event.getEntity() != null ? event.getEntity().getLocation() : null,
                plugin.flags.gameEvents
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockFading));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(isInteractionRestricted(
                event.getBlock().getLocation(),
                event.getNewState().getLocation(),
                plugin.flags.blockForming
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(isInteractionRestricted(
                Objects.requireNonNullElse(
                        event.getIgnitingBlock(),
                        event.getBlock()
                ).getLocation(),
                event.getBlock().getLocation(),
                plugin.flags.blockBurning
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(isInteractionRestricted(
                event.getIgnitingBlock() != null ? event.getIgnitingBlock().getLocation()
                        : event.getIgnitingEntity() != null ? event.getIgnitingEntity().getLocation()
                        : event.getBlock().getLocation(),
                event.getBlock().getLocation(),
                plugin.flags.blockIgniting
        ));
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
    public void onLiquidFlow(BlockFromToEvent event) {
        if (!event.getBlock().isLiquid()) return;
        event.setCancelled(isInteractionRestricted(
                event.getBlock().getLocation(),
                event.getToBlock().getLocation(),
                plugin.flags.liquidFlow
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.blockGrowth));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        filterStates(event.getBlock().getLocation(), event.getBlocks(), null, plugin.flags.blockAbsorb);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        filterStates(event.getLocation(), event.getBlocks(), event.getPlayer(), plugin.flags.blockGrowth);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        filterStates(event.getBlock().getLocation(), event.getBlocks(), event.getPlayer(), plugin.flags.blockFertilize);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        filterBlocks(event.getBlock().getLocation(), event.blockList(), plugin.flags.explosions);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        filterBlocks(event.getLocation(), event.blockList(), plugin.flags.explosions);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        if (!area.getFlag(plugin.flags.redstone)) event.setNewCurrent(event.getOldCurrent());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(!plugin.protectionService().canDestroy(event.getPlayer(), event.getBlock().getLocation()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getBlock()), "area.failed.break");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(!plugin.protectionService().canDestroy(event.getPlayer(), event.getBlock().getLocation()));
        plugin.failed(event.getPlayer(), event, plugin.areaProvider().getArea(event.getBlock()), "area.failed.place");
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(isInteractionRestricted(
                event.getSource().getLocation(),
                event.getBlock().getLocation(),
                plugin.flags.blockSpread
        ));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        event.setCancelled(!area.getFlag(plugin.flags.leavesDecay));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        var flag = switch (event.getReason()) {
            case EXTINGUISH -> plugin.flags.cauldronExtinguishEntity;
            case EVAPORATE -> plugin.flags.cauldronEvaporation;
            case NATURAL_FILL -> plugin.flags.naturalCauldronFill;
            default -> null;
        };
        if (flag != null) event.setCancelled(!area.getFlag(flag));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        handlePistonMovement(event, event.getDirection(), event.getBlocks(), 1);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        handlePistonMovement(event, event.getDirection(), event.getBlocks(), 0);
    }

    private void handlePistonMovement(BlockPistonEvent event, BlockFace direction, List<Block> blocks, int offset) {
        var area = plugin.areaProvider().getArea(event.getBlock());
        var relative = plugin.areaProvider().getArea(event.getBlock().getRelative(direction));
        event.setCancelled(!area.canInteract(relative) || !blocks.stream().allMatch(block ->
                area.canInteract(plugin.areaProvider().getArea(block.getRelative(direction, offset)))));
    }

    private boolean isInteractionRestricted(Location source, @Nullable Location target, Flag<Boolean> flag) {
        var area = plugin.areaProvider().getArea(source);
        if (!area.getFlag(flag)) return true;
        return target != null && !source.equals(target) && !area.canInteract(plugin.areaProvider().getArea(target));
    }

    private void filterStates(Location source, List<BlockState> blocks, @Nullable Player player, Flag<Boolean> flag) {
        filter(source, blocks, BlockState::getLocation, player, flag);
    }

    private void filterBlocks(Location source, List<Block> blocks, Flag<Boolean> flag) {
        filter(source, blocks, Block::getLocation, null, flag);
    }

    private <T> void filter(Location source, List<T> list, Function<T, Location> function, @Nullable Player player, Flag<Boolean> flag) {
        filter(source, list, function, (area, target) -> {
            if (player == null) return area.canInteract(target) && target.getFlag(flag);
            return target.isPermitted(player.getUniqueId()) || target.getFlag(flag);
        });
    }

    private <T> void filter(Location source, List<T> list, Function<T, Location> function, BiPredicate<Area, Area> filter) {
        var area = plugin.areaProvider().getArea(source);
        list.removeIf(object -> !filter.test(area, plugin.areaProvider().getArea(function.apply(object))));
    }
}
