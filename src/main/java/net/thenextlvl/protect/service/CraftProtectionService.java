package net.thenextlvl.protect.service;

import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static org.bukkit.entity.EntityType.ARMOR_STAND;
import static org.bukkit.entity.EntityType.PLAYER;

@NullMarked
public final class CraftProtectionService implements ProtectionService {
    private final ProtectPlugin plugin;

    public CraftProtectionService(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canEdit(@Nullable final Entity entity, final Area area) {
        return canPlace(entity, area) && canDestroy(entity, area);
    }

    @Override
    public boolean canEdit(@Nullable final Entity entity, final Location location) {
        return canEdit(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canPlace(@Nullable final Entity entity, final Area area) {
        return canPerformAction(entity, area, plugin.flags.place, "protect.bypass.place");
    }

    @Override
    public boolean canPlace(@Nullable final Entity entity, final Location location) {
        return canPlace(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canDestroy(@Nullable final Entity entity, final Area area) {
        return canPerformAction(entity, area, plugin.flags.destroy, "protect.bypass.destroy");
    }

    @Override
    public boolean canDestroy(@Nullable final Entity entity, final Location location) {
        return canDestroy(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canInteract(@Nullable final Entity entity, final Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.interact, "protect.bypass.interact");
    }

    @Override
    public boolean canInteract(@Nullable final Entity entity, final Entity interacted) {
        final var flag = interacted.getType().equals(ARMOR_STAND) ? plugin.flags.armorStandManipulate : plugin.flags.entityInteract;
        return canPerformAction(entity, plugin.areaProvider().getArea(interacted), flag, "protect.bypass.entity-interact");
    }

    @Override
    public boolean canInteractPhysical(@Nullable final Entity entity, final Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.physicalInteract, "protect.bypass.physical-interact");
    }

    @Override
    public boolean canAttack(final Entity attacker, final Entity victim) {
        final var flag = attacker.getType().equals(PLAYER) && victim.getType().equals(PLAYER)
                ? plugin.flags.playerAttackPlayer : attacker.getType().equals(PLAYER)
                ? plugin.flags.playerAttackEntity : victim.getType().equals(PLAYER)
                ? plugin.flags.entityAttackPlayer : plugin.flags.entityAttackEntity;

        final var first = plugin.areaProvider().getArea(attacker);
        final var second = plugin.areaProvider().getArea(victim);

        if ((first.getFlag(flag) || first.isPermitted(attacker))
            && (second.getFlag(flag) || second.isPermitted(attacker))) return true;

        if (first.canInteract(second)
            && (first.getFlag(flag) || first.isPermitted(attacker))
            || (second.getFlag(flag) || second.isPermitted(attacker))
        ) return true;

        if (!attacker.hasPermission("protect.bypass.attack")) return false;
        return attacker instanceof final Player player && player.getGameMode().isInvulnerable();
    }

    @Override
    public boolean canShear(@Nullable final Entity entity, final Entity sheared) {
        return canPerformAction(entity, plugin.areaProvider().getArea(sheared),
                plugin.flags.entityShear, "protect.bypass.entity-shear");
    }

    @Override
    public boolean canTrample(@Nullable final Entity entity, final Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.cropTrample, "protect.bypass.trample");
    }

    @Override
    public boolean canEnter(@Nullable final Entity entity, final Area area) {
        return canPerformAction(entity, area, plugin.flags.areaEnter, "protect.bypass.enter");
    }

    @Override
    public boolean canLeave(@Nullable final Entity entity, final Area area) {
        return canPerformAction(entity, area, plugin.flags.areaLeave, "protect.bypass.leave");
    }

    @Override
    public boolean canPerformAction(@Nullable final Entity entity, final Area area, final Flag<Boolean> flag, @Nullable final String permission) {
        return area.getFlag(flag) || entity != null && (area.isPermitted(entity)
                || (permission != null && entity.hasPermission(permission)
                && (!(entity instanceof final Player player) || player.getGameMode().isInvulnerable())));
    }
}
