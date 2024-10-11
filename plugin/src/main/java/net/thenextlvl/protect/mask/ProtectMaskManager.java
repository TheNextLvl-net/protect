package net.thenextlvl.protect.mask;

import com.fastasyncworldedit.core.configuration.Settings;
import com.fastasyncworldedit.core.regions.FaweMask;
import com.fastasyncworldedit.core.regions.FaweMaskManager;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionIntersection;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.GlobalArea;
import net.thenextlvl.protect.area.RegionizedArea;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ProtectMaskManager extends FaweMaskManager {
    private static final CuboidRegion GLOBAL = new CuboidRegion(
            BlockVector3.at(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
            BlockVector3.at(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
    );
    private final ProtectPlugin plugin;

    public ProtectMaskManager(ProtectPlugin plugin) {
        super(plugin.getName());
        this.plugin = plugin;
    }

    @Override
    public FaweMask getMask(Player player, MaskType type, boolean isWhitelist) {
        if (isWhitelist && Settings.settings().REGION_RESTRICTIONS_OPTIONS.WORLDGUARD_REGION_BLACKLIST) {
            //return new FaweMask(GLOBAL);
        }

        var bukkit = wrap(player);
        if (bukkit == null) return null;

        var location = bukkit.getLocation();

        var areas = plugin.areaProvider().getAreas(location).toList();
        if (!areas.isEmpty()) {
            var result = new HashSet<Region>();
            for (var area : areas) {
                if (area instanceof GlobalArea) {
                    return new FaweMask(GLOBAL) {
                        @Override
                        public boolean isValid(Player player, MaskType type) {
                            var wrapped = wrap(player);
                            return wrapped != null && plugin.protectionService().canEdit(wrapped, area);
                        }
                    };
                } else if (area instanceof RegionizedArea<?> regionized) {
                    result.add(regionized.getRegion());
                }
            }
            return new FaweMask(new RegionIntersection(player.getWorld(), result)) {
                @Override
                public boolean isValid(Player player, MaskType type) {
                    var wrapped = wrap(player);
                    if (wrapped == null) return false;
                    for (var area : areas) {
                        if (!plugin.protectionService().canEdit(wrapped, area)) return false;
                    }
                    return true;
                }
            };
        }
        return null;
    }

    private @Nullable org.bukkit.entity.Player wrap(Player player) {
        return plugin.getServer().getPlayer(player.getUniqueId());
    }
}
