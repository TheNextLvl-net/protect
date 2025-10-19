package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.jspecify.annotations.Nullable;

public final class PhysicsListener implements Listener {
    private final ProtectPlugin plugin;

    public PhysicsListener(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPhysics(BlockPhysicsEvent event) {

        if ((event.getSourceBlock().isEmpty() && event.getChangedType().isAir()) || (
                event.getSourceBlock().getType().equals(Material.SNOW)
                || event.getSourceBlock().getType().equals(Material.POWDER_SNOW)
                || event.getSourceBlock().getType().equals(Material.SNOW_BLOCK))) {
            if (event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.GRASS_BLOCK)) {
                return;
            }
        }

        if (event.getChangedType().equals(Material.IRON_BARS)
            || event.getChangedBlockData() instanceof Stairs
            || event.getChangedBlockData() instanceof Chest
            || event.getChangedBlockData() instanceof Fence
            || event.getChangedBlockData() instanceof GlassPane
            || event.getChangedBlockData() instanceof Wall
            || event.getChangedBlockData() instanceof Door) {
            return;
        }

        if (event.getChangedBlockData() instanceof Powerable && !event.getBlock().isEmpty()) return;

        event.setCancelled(isInteractionRestricted(event.getSourceBlock(), event.getBlock(),
                event.getChangedType().hasGravity() ? plugin.flags.gravity : plugin.flags.physics));
    }

    private boolean isInteractionRestricted(Block source, @Nullable Block target, Flag<Boolean> flag) {
        var area = plugin.areaProvider().getArea(source);
        if (!area.getFlag(flag)) return true;
        return target != null && !source.equals(target) && !area.canInteract(plugin.areaProvider().getArea(target));
    }
}
