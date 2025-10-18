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
public class CraftProtectionService implements ProtectionService {
    private final ProtectPlugin plugin;

    public CraftProtectionService(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canEdit(@Nullable Entity entity, Area area) {
        return canPlace(entity, area) && canDestroy(entity, area);
    }

    @Override
    public boolean canEdit(@Nullable Entity entity, Location location) {
        return canEdit(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canPlace(@Nullable Entity entity, Area area) {
        return canPerformAction(entity, area, plugin.flags.place, "protect.bypass.place");
    }

    @Override
    public boolean canPlace(@Nullable Entity entity, Location location) {
        return canPlace(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canDestroy(@Nullable Entity entity, Area area) {
        return canPerformAction(entity, area, plugin.flags.destroy, "protect.bypass.destroy");
    }

    @Override
    public boolean canDestroy(@Nullable Entity entity, Location location) {
        return canDestroy(entity, plugin.areaProvider().getArea(location));
    }

    @Override
    public boolean canInteract(@Nullable Entity entity, Location location) {
        return canPerformAction(entity, plugin.areaProvider().getArea(location),
                plugin.flags.interact, "protect.bypass.interact");
    }

    @Override
    public boolean canInteract(@Nullable Entity entity, Entity interacted) {
        var flag = interacted.getType().equals(ARMOR_STAND) ? plugin.flags.armorStandManipulate : plugin.flags.entityInteract;
        return canPerformAction(entity, plugin.areaProvider().getArea(interacted), flag, "protect.bypass.entity-interact");
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
        return area.getFlag(flag) || entity != null && (area.isPermitted(entity.getUniqueId())
                || (permission != null && entity.hasPermission(permission)
                && (!(entity instanceof Player player) || player.getGameMode().isInvulnerable())));
    }
}
