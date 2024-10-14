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
    public boolean canEdit(Player player, Area area) {
        return canBuild(player, area) && canBreak(player, area);
    }

    @Override
    public boolean canBuild(Player player, Area area) {
        return canPerformAction(player, area, plugin.flags.blockPlace, "protect.bypass.build");
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        return canBuild(player, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canBreak(Player player, Area area) {
        return canPerformAction(player, area, plugin.flags.blockBreak, "protect.bypass.break");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return canBreak(player, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canEmptyBucket(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.emptyBucket, "protect.bypass.empty-bucket");
    }

    @Override
    public boolean canFillBucket(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.fillBucket, "protect.bypass.fill-bucket");
    }

    @Override
    public boolean canEmptyBottle(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.emptyBottle, "protect.bypass.empty-bottle");
    }

    @Override
    public boolean canFillBottle(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.fillBottle, "protect.bypass.fill-bottle");
    }

    @Override
    public boolean canWashBanner(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.bannerWashing, "protect.bypass.wash-banner");
    }

    @Override
    public boolean canWashShulker(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.shulkerWashing, "protect.bypass.wash-shulker");
    }

    @Override
    public boolean canWashArmor(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.armorWashing, "protect.bypass.wash-armor");
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
    public boolean canInteractPhysical(Player player, Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.physicalInteract, "protect.bypass.physical-interact");
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
               || (area instanceof RegionizedArea<?> regionized && (player.getUniqueId().equals(regionized.getOwner())
                                                                    || regionized.isMember(player.getUniqueId())))
               || area.getFlag(flag);
    }
}
