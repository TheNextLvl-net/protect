package net.thenextlvl.protect.service;

import core.annotation.ParametersAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@ParametersAreNotNullByDefault
public class CraftProtectionService implements ProtectionService {
    private final ProtectPlugin plugin;

    @Override
    public boolean canBuild(Player player, Location location) {
        return plugin.areaProvider().getArea(location).getFlag(plugin.flags.blockPlace)
                || player.hasPermission("protect.bypass.build");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return plugin.areaProvider().getArea(location).getFlag(plugin.flags.blockBreak)
                || player.hasPermission("protect.bypass.break");
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return plugin.areaProvider().getArea(location).getFlag(plugin.flags.interact)
                || player.hasPermission("protect.bypass.interact");
    }

    @Override
    public boolean canInteract(Player player, Entity entity) {
        return plugin.areaProvider().getArea(entity).getFlag(plugin.flags.entityInteract)
                || player.hasPermission("protect.bypass.entity-interact");
    }

    @Override
    public boolean canShear(Player player, Entity entity) {
        return plugin.areaProvider().getArea(entity).getFlag(plugin.flags.entityShear)
                || player.hasPermission("protect.bypass.entity-shear");
    }

    @Override
    public boolean canTrample(Player player, Location location) {
        return plugin.areaProvider().getArea(location).getFlag(plugin.flags.cropTrample)
                || player.hasPermission("protect.bypass.trample");
    }

    @Override
    public boolean canEnter(Player player, Area area) {
        return player.hasPermission("protect.bypass.enter") || area.getFlag(plugin.flags.areaEnter);
    }

    @Override
    public boolean canLeave(Player player, Area area) {
        return player.hasPermission("protect.bypass.leave") || area.getFlag(plugin.flags.areaLeave);
    }
}
