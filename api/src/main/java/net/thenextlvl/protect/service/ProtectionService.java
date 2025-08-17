package net.thenextlvl.protect.service;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The ProtectionService interface provides methods to determine whether an entity
 * is permitted to perform certain actions in certain locations or areas.
 */
@NullMarked
public interface ProtectionService {
    /**
     * Determines whether an entity can use worldedit in a specific area.
     *
     * @param entity the entity attempting to use worldedit
     * @param area   the area where the entity intends to use worldedit
     * @return true if the entity can use worldedit in the area, false otherwise
     */
    boolean canEdit(@Nullable Entity entity, Area area);

    /**
     * Determines whether an entity can use worldedit in a specific area.
     *
     * @param entity   the entity attempting to use worldedit
     * @param location location where the entity intends to use worldedit
     * @return true if the entity can use worldedit in the area, false otherwise
     */
    boolean canEdit(@Nullable Entity entity, Location location);

    /**
     * Determines whether an entity can place something in a given area.
     *
     * @param entity the entity that wants to place something
     * @param area   the area where the entity intends to place something
     * @return true if the entity can place in this area, false otherwise
     */
    boolean canPlace(@Nullable Entity entity, Area area);

    /**
     * Determines whether an entity can place something at a given location.
     *
     * @param entity   the entity that wants to place something
     * @param location the location where the entity intends to place something
     * @return true if the entity can place in this area, false otherwise
     */
    boolean canPlace(@Nullable Entity entity, Location location);

    /**
     * Determines whether an entity can destroy something in a given area.
     *
     * @param entity the entity that wants to destroy something
     * @param area   the area of the object the entity intends to destroy
     * @return true if the entity can destroy the object in this area, false otherwise
     */
    boolean canDestroy(@Nullable Entity entity, Area area);

    /**
     * Determines whether an entity can destroy something at a given location.
     *
     * @param entity   the entity that wants to destroy something
     * @param location the location of the object the entity intends to destroy
     * @return true if the entity can destroy the object at the location, false otherwise
     */
    boolean canDestroy(@Nullable Entity entity, Location location);

    /**
     * Determines whether an entity can interact with an object at a given location.
     *
     * @param entity   the entity that wants to interact
     * @param location the location of the object the entity intends to interact with
     * @return true if the entity can interact with the object at the location, false otherwise
     */
    boolean canInteract(@Nullable Entity entity, Location location);

    /**
     * Determines whether an entity can interact with an entity.
     *
     * @param entity     the entity that wants to interact
     * @param interacted the entity that is intended to be interacted with
     * @return true if the entity can interact with the object at the location, false otherwise
     */
    boolean canInteract(@Nullable Entity entity, Entity interacted);

    /**
     * Determines whether an entity can physically interact with an object at a given location.
     *
     * @param entity   the entity that wants to interact
     * @param location the location of the object the entity intends to interact with
     * @return true if the entity can physically interact with the object at the location, false otherwise
     */
    boolean canInteractPhysical(@Nullable Entity entity, Location location);

    /**
     * Determines whether an entity can attack another entity.
     *
     * @param attacker the entity attempting to launch the attack
     * @param victim   the entity that is the target of the attack
     * @return true if the attack is permissible, false otherwise
     */
    boolean canAttack(Entity attacker, Entity victim);

    /**
     * Determines whether an entity can shear an entity.
     *
     * @param entity  the entity that wants to shear
     * @param sheared the entity that is intended to be sheared
     * @return true if the entity can shear the entity, false otherwise
     */
    boolean canShear(@Nullable Entity entity, Entity sheared);

    /**
     * Determines whether an entity can trample a block at a given location.
     *
     * @param entity   the entity attempting to trample the block
     * @param location the location of the block to trample
     * @return true if the entity can trample the block at the location, false otherwise
     */
    boolean canTrample(@Nullable Entity entity, Location location);

    /**
     * Determines whether an entity can enter an area.
     *
     * @param entity the entity that wants to enter the area
     * @param area   the area to enter
     * @return true if the entity can enter the area, false otherwise
     */
    boolean canEnter(@Nullable Entity entity, Area area);

    /**
     * Determines whether an entity can leave an area.
     *
     * @param entity the entity that wants to leave the area
     * @param area   the area to leave
     * @return true if the entity can leave the area, false otherwise
     */
    boolean canLeave(@Nullable Entity entity, Area area);

    /**
     * Determines whether an entity can perform a specific action in a given area.
     *
     * @param entity     the entity attempting to perform the action
     * @param area       the area where the entity intends to perform the action
     * @param flag       the flag indicating specific conditions or constraints for the action
     * @param permission an optional permission string required to perform the action
     * @return true if the entity can perform the action in the area, false otherwise
     */
    boolean canPerformAction(@Nullable Entity entity, Area area, Flag<Boolean> flag, @Nullable String permission);
}
