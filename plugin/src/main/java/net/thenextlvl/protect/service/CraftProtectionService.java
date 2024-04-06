package net.thenextlvl.protect.service;

import core.annotation.ParametersAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@ParametersAreNotNullByDefault
public class CraftProtectionService implements ProtectionService {
    private final ProtectPlugin plugin;

    @Override
    public boolean canBuild(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.blockPlace, "protect.bypass.build");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.blockBreak, "protect.bypass.break");
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.interact, "protect.bypass.interact");
    }

    @Override
    public boolean canInteract(Player player, Entity entity) {
        return canPerformAction(player, plugin.areaProvider().getArea(entity),
                plugin.flags.entityInteract, "protect.bypass.entity-interact");
    }

    @Override
    public boolean canShear(Player player, Entity entity) {
        return canPerformAction(player, plugin.areaProvider().getArea(entity),
                plugin.flags.entityShear, "protect.bypass.entity-shear");
    }

    @Override
    public boolean canTrample(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.cropTrample, "protect.bypass.trample");
    }

    @Override
    public boolean canEnter(Player player, Area area) {
        return canPerformAction(player, area, plugin.flags.areaEnter, "protect.bypass.enter");
    }

    @Override
    public boolean canLeave(Player player, Area area) {
        return canPerformAction(player, area, plugin.flags.areaLeave, "protect.bypass.leave");
    }

    private boolean canPerformAction(Player player, Area area, Flag<Boolean> flag, String permission) {
        return (player.hasPermission(permission) && player.getGameMode().equals(GameMode.CREATIVE))
                || (area instanceof RegionizedArea<?> regionized && player.getUniqueId().equals(regionized.getOwner()))
                || area.getFlag(flag);
    }
}
