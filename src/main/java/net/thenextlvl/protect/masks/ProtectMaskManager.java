package net.thenextlvl.protect.masks;

import com.fastasyncworldedit.bukkit.regions.BukkitMaskManager;
import com.fastasyncworldedit.core.configuration.Settings;
import com.fastasyncworldedit.core.regions.FaweMask;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import com.sk89q.worldedit.world.World;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GlobalArea;
import net.thenextlvl.protect.area.RegionizedArea;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NullMarked
public final class ProtectMaskManager extends BukkitMaskManager {
    private static final CuboidRegion GLOBAL = new CuboidRegion(
            BlockVector3.at(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
            BlockVector3.at(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
    );
    private final ProtectPlugin plugin;

    public ProtectMaskManager(final ProtectPlugin plugin) {
        super(plugin.getName());
        this.plugin = plugin;
    }

    @Override
    public @Nullable FaweMask getMask(final Player player, final MaskType type, final boolean whitelist) {
        return getMask(player, type, whitelist, true);
    }

    @Override
    public @Nullable FaweMask getMask(final Player player, final MaskType type, final boolean whitelist, final boolean notify) {
        if (whitelist && Settings.settings().REGION_RESTRICTIONS_OPTIONS.WORLDGUARD_REGION_BLACKLIST)
            return new FaweMask(GLOBAL);

        final var bukkit = plugin.getServer().getPlayer(player.getUniqueId());
        if (bukkit == null) return null;

        final var areas = getAreas(bukkit, whitelist);
        if (areas.isEmpty()) return null;

        final var regions = new ArrayList<Region>();
        for (final var area : areas) {
            if (area instanceof final RegionizedArea<?> regionized) regions.add(regionized.getRegion());
            else if (area instanceof GlobalArea) regions.add(GLOBAL);
        }

        return new RegionMask(bukkit, regions, areas, player.getWorld());
    }

    private final class RegionMask extends FaweMask {
        private final org.bukkit.entity.Player player;
        private final Collection<Area> areas;

        public RegionMask(
                final org.bukkit.entity.Player player,
                final Collection<Region> regions,
                final Collection<Area> areas,
                final World world
        ) {
            super(new RegionIntersection(world, regions));
            this.player = player;
            this.areas = areas;
        }

        @Override
        public boolean isValid(final Player ignored, final MaskType type) {
            return areas.stream().allMatch(area -> plugin.protectionService().canEdit(player, area));
        }
    }

    public List<Area> getAreas(final org.bukkit.entity.Player player, final boolean whitelist) {
        return plugin.areaProvider().getAreas(player.getWorld())
                .filter(area -> whitelist == plugin.protectionService().canEdit(player, area))
                .toList();
    }
}
