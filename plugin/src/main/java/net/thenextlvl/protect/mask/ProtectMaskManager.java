package net.thenextlvl.protect.mask;

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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProtectMaskManager extends BukkitMaskManager {
    private final ProtectPlugin plugin;

    public ProtectMaskManager(ProtectPlugin plugin) {
        super(plugin.getName());
        this.plugin = plugin;
    }

    @Override
    public @Nullable FaweMask getMask(Player player, MaskType type, boolean isWhitelist) {
        return getMask(player, type, isWhitelist, true);
    }

    @Override
    public @Nullable FaweMask getMask(Player player, MaskType type, boolean whitelist, boolean notify) {
        if (whitelist && Settings.settings().REGION_RESTRICTIONS_OPTIONS.WORLDGUARD_REGION_BLACKLIST)
            return new FaweMask(getRegion(player.getWorld()));

        var bukkit = plugin.getServer().getPlayer(player.getUniqueId());
        if (bukkit == null) return null;

        var areas = getAreas(bukkit, whitelist);
        if (areas.isEmpty()) return null;

        var regions = new ArrayList<Region>();
        for (var area : areas) {
            if (area instanceof GlobalArea) {
                regions.add(getRegion(player.getWorld()));
            } else if (area instanceof RegionizedArea<?> regionized) {
                regions.add(regionized.getRegion());
            }
        }

        return new FaweMask(new RegionIntersection(player.getWorld(), regions)) {
            @Override
            public boolean isValid(Player player, MaskType type) {
                for (var area : areas) if (!plugin.protectionService().canEdit(bukkit, area)) return false;
                return true;
            }
        };
    }

    private CuboidRegion getRegion(World world) {
        return new CuboidRegion(world,
                BlockVector3.at(Integer.MIN_VALUE, world.getMinY(), Integer.MIN_VALUE),
                BlockVector3.at(Integer.MAX_VALUE, world.getMaxY(), Integer.MAX_VALUE)
        );
    }

    public List<Area> getAreas(org.bukkit.entity.Player player, boolean whitelist) {
        return plugin.areaProvider().getAreas(player.getWorld())
                .filter(area -> whitelist == plugin.protectionService().canEdit(player, area))
                .sorted(Comparator.comparingInt(Area::getPriority))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isExclusive() {
        return true;
    }
}
