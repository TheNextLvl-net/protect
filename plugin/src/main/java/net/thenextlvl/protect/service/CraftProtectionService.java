package net.thenextlvl.protect.service;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static org.bukkit.entity.EntityType.PLAYER;

@NullMarked
@RequiredArgsConstructor
public class CraftProtectionService implements ProtectionService {
    private final ProtectPlugin plugin;

    @Override
    public boolean canEdit(@Nullable Entity entity, Area area) {
        return canPlace(entity, area) && canBreak(entity, area);
    }

    @Override
    public boolean canEdit(@Nullable Entity entity, Location location) {
        return canEdit(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canPlace(@Nullable Entity entity, Area area) {
        return canPerformAction(entity, area, plugin.flags.placement, "protect.bypass.build");
    }

    @Override
    public boolean canPlace(@Nullable Entity entity, Location location) {
        return canPlace(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canBreak(@Nullable Entity entity, Area area) {
        return canPerformAction(entity, area, plugin.flags.blockBreak, "protect.bypass.break");
    }

    @Override
    public boolean canBreak(@Nullable Entity entity, Location location) {
        return canBreak(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canEmptyBucket(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.emptyBucket, "protect.bypass.empty-bucket");
    }

    @Override
    public boolean canFillBucket(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.fillBucket, "protect.bypass.fill-bucket");
    }

    @Override
    public boolean canEmptyBottle(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.emptyBottle, "protect.bypass.empty-bottle");
    }

    @Override
    public boolean canFillBottle(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.fillBottle, "protect.bypass.fill-bottle");
    }

    @Override
    public boolean canWashBanner(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.bannerWashing, "protect.bypass.wash-banner");
    }

    @Override
    public boolean canWashShulker(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.shulkerWashing, "protect.bypass.wash-shulker");
    }

    @Override
    public boolean canWashArmor(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.armorWashing, "protect.bypass.wash-armor");
    }

    @Override
    public boolean canInteract(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.interact, "protect.bypass.interact");
    }

    @Override
    public boolean canInteract(@Nullable Entity entity, Entity interacted) {
        return canPerformAction(entity, plugin.areaProvider().getArea(interacted),
                plugin.flags.entityInteract, "protect.bypass.entity-interact");
    }

    @Override
    public boolean canInteractPhysical(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.physicalInteract, "protect.bypass.physical-interact");
    }

    @Override
    public boolean canAttack(Entity attacker, Entity victim) {
        var flag = attacker.getType().equals(PLAYER) && victim.getType().equals(PLAYER)
                ? plugin.flags.playerAttackPlayer : attacker.getType().equals(PLAYER)
                ? plugin.flags.playerAttackEntity : victim.getType().equals(PLAYER)
                ? plugin.flags.entityAttackPlayer : plugin.flags.entityAttackEntity;

        var first = plugin.areaProvider().getArea(attacker);
        var second = plugin.areaProvider().getArea(victim);

        if ((first.getFlag(flag) || first.isPermitted(attacker.getUniqueId()))
            && (second.getFlag(flag) || second.isPermitted(attacker.getUniqueId()))) return true;

        if (first.canInteract(second)
            && (first.getFlag(flag) || first.isPermitted(attacker.getUniqueId()))
            || (second.getFlag(flag) || second.isPermitted(attacker.getUniqueId()))
        ) return true;

        if (!attacker.hasPermission("protect.bypass.attack")) return false;
        return attacker instanceof Player player && player.getGameMode().isInvulnerable();
    }

    @Override
    public boolean canShear(@Nullable Entity entity, Entity sheared) {
        return canPerformAction(entity, plugin.areaProvider().getArea(sheared),
                plugin.flags.entityShear, "protect.bypass.entity-shear");
    }

    @Override
    public boolean canTrample(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.cropTrample, "protect.bypass.trample");
    }

    @Override
    public boolean canEnter(@Nullable Entity entity, Area area) {
        return canPerformAction(entity, area, plugin.flags.areaEnter, "protect.bypass.enter");
    }

    @Override
    public boolean canLeave(@Nullable Entity entity, Area area) {
        return canPerformAction(entity, area, plugin.flags.areaLeave, "protect.bypass.leave");
    }

    @Override
    public boolean canPerformAction(@Nullable Entity entity, Area area, Flag<Boolean> flag, @Nullable String permission) {
        return area.getFlag(flag) || entity != null && (area.isPermitted(entity.getUniqueId()) || (
                permission != null && entity.hasPermission(permission)
                && (!(entity instanceof Player player) || player.getGameMode().isInvulnerable())
        ));
    }
}
