package net.thenextlvl.protect.service;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class CraftProtectionService implements ProtectionService {
    private final @NotNull ProtectPlugin plugin;

    @Override
    public boolean canEdit(@NotNull Player player, @NotNull Area area) {
        return canBuild(player, area) && canBreak(player, area);
    }

    @Override
    public boolean canBuild(@NotNull Player player, @NotNull Area area) {
        return canPerformAction(player, area, plugin.flags.blockPlace, "protect.bypass.build");
    }

    @Override
    public boolean canBuild(@NotNull Player player, @NotNull Location location) {
        return canBuild(player, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canBreak(@NotNull Player player, @NotNull Area area) {
        return canPerformAction(player, area, plugin.flags.blockBreak, "protect.bypass.break");
    }

    @Override
    public boolean canBreak(@NotNull Player player, @NotNull Location location) {
        return canBreak(player, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canEmptyBucket(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.emptyBucket, "protect.bypass.empty-bucket");
    }

    @Override
    public boolean canFillBucket(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.fillBucket, "protect.bypass.fill-bucket");
    }

    @Override
    public boolean canEmptyBottle(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.emptyBottle, "protect.bypass.empty-bottle");
    }

    @Override
    public boolean canFillBottle(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.fillBottle, "protect.bypass.fill-bottle");
    }

    @Override
    public boolean canWashBanner(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.bannerWashing, "protect.bypass.wash-banner");
    }

    @Override
    public boolean canWashShulker(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.shulkerWashing, "protect.bypass.wash-shulker");
    }

    @Override
    public boolean canWashArmor(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.armorWashing, "protect.bypass.wash-armor");
    }

    @Override
    public boolean canInteract(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.interact, "protect.bypass.interact");
    }

    @Override
    public boolean canInteract(@NotNull Player player, @NotNull Entity entity) {
        return canPerformAction(player, plugin.areaProvider().getArea(entity),
                plugin.flags.entityInteract, "protect.bypass.entity-interact");
    }

    @Override
    public boolean canInteractPhysical(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.physicalInteract, "protect.bypass.physical-interact");
    }

    @Override
    public boolean canShear(@NotNull Player player, @NotNull Entity entity) {
        return canPerformAction(player, plugin.areaProvider().getArea(entity),
                plugin.flags.entityShear, "protect.bypass.entity-shear");
    }

    @Override
    public boolean canTrample(@NotNull Player player, @NotNull Location location) {
        return canPerformAction(player, plugin.areaProvider().getArea(location),
                plugin.flags.cropTrample, "protect.bypass.trample");
    }

    @Override
    public boolean canEnter(@NotNull Player player, @NotNull Area area) {
        return canPerformAction(player, area, plugin.flags.areaEnter, "protect.bypass.enter");
    }

    @Override
    public boolean canLeave(@NotNull Player player, @NotNull Area area) {
        return canPerformAction(player, area, plugin.flags.areaLeave, "protect.bypass.leave");
    }

    private boolean canPerformAction(@NotNull Player player, @NotNull Area area, @NotNull Flag<Boolean> flag, @NotNull String permission) {
        return (player.hasPermission(permission) && player.getGameMode().isInvulnerable())
               || area.isPermitted(player.getUniqueId())
               || area.getFlag(flag);
    }
}
