package net.thenextlvl.protect.service;

import net.thenextlvl.protect.area.Area;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * The ProtectionService interface provides methods to determine whether a player
 * is permitted to perform certain actions in certain locations or areas.
 */
@NullMarked
public interface ProtectionService {
    /**
     * Determines whether a player can use worldedit in a specific area.
     *
     * @param player the player attempting to use worldedit
     * @param area   the area where the player intends to use worldedit
     * @return true if the player can use worldedit in the area, false otherwise
     */
    boolean canEdit(Player player, Area area);

    /**
     * Determines whether a player can build in a given area.
     *
     * @param player the player who wants to build
     * @param area   the area where the player intends to build
     * @return true if the player can build in this area, false otherwise
     */
    boolean canBuild(Player player, Area area);

    /**
     * Determines whether a player can build at a given location.
     *
     * @param player   the player who wants to build
     * @param location the location where the player intends to build
     * @return true if the player can build at the location, false otherwise
     */
    boolean canBuild(Player player, Location location);

    /**
     * Determines whether a player can break a block in a given area.
     *
     * @param player the player who wants to break the block
     * @param area   the area of the block the player intends to break
     * @return true if the player can break the block in this area, false otherwise
     */
    boolean canBreak(Player player, Area area);

    /**
     * Determines whether a player can break a block at a given location.
     *
     * @param player   the player who wants to break the block
     * @param location the location of the block the player intends to break
     * @return true if the player can break the block at the location, false otherwise
     */
    boolean canBreak(Player player, Location location);

    /**
     * Determines whether a player can empty a bucket at a given location.
     *
     * @param player   the player who wants to empty a bucket
     * @param location the location where the player intends to empty the bucket
     * @return true if the player can empty the bucket at the location, false otherwise
     */
    boolean canEmptyBucket(Player player, Location location);

    /**
     * Determines whether a player can fill a bucket at a given location.
     *
     * @param player   the player who wants to fill a bucket
     * @param location the location where the player intends to fill the bucket
     * @return true if the player can fill the bucket at the location, false otherwise
     */
    boolean canFillBucket(Player player, Location location);

    /**
     * Determines whether a player can empty a bottle at a given location.
     *
     * @param player   the player who wants to empty a bottle
     * @param location the location where the player intends to empty the bottle
     * @return true if the player can empty the bottle at the location, false otherwise
     */
    boolean canEmptyBottle(Player player, Location location);

    /**
     * Determines whether a player can fill a bottle at a given location.
     *
     * @param player   the player who wants to fill a bottle
     * @param location the location where the player intends to fill the bottle
     * @return true if the player can fill the bottle at the location, false otherwise
     */
    boolean canFillBottle(Player player, Location location);

    /**
     * Determines whether a player can wash a banner at a given location.
     *
     * @param player   the player who wants to wash a banner
     * @param location the location where the player intends to wash the banner
     * @return true if the player can wash the banner at the location, false otherwise
     */
    boolean canWashBanner(Player player, Location location);

    /**
     * Determines whether a player can wash a shulker at a given location.
     *
     * @param player   the player who wants to wash a shulker
     * @param location the location where the player intends to wash the shulker
     * @return true if the player can wash the shulker at the location, false otherwise
     */
    boolean canWashShulker(Player player, Location location);

    /**
     * Determines whether a player can wash an armor at a given location.
     *
     * @param player   the player who wants to wash an armor
     * @param location the location where the player intends to wash the armor
     * @return true if the player can wash the armor at the location, false otherwise
     */
    boolean canWashArmor(Player player, Location location);

    /**
     * Determines whether a player can interact with an object at a given location.
     *
     * @param player   the player who wants to interact
     * @param location the location of the object the player intends to interact with
     * @return true if the player can interact with the object at the location, false otherwise
     */
    boolean canInteract(Player player, Location location);

    /**
     * Determines whether a player can interact with an entity.
     *
     * @param player the player who wants to interact
     * @param entity the entity the player intends to interact with
     * @return true if the player can interact with the object at the location, false otherwise
     */
    boolean canInteract(Player player, Entity entity);

    /**
     * Determines whether a player can physically interact with an object at a given location.
     *
     * @param player   the player who wants to interact
     * @param location the location of the object the player intends to interact with
     * @return true if the player can physically interact with the object at the location, false otherwise
     */
    boolean canInteractPhysical(Player player, Location location);

    /**
     * Determines whether a player can shear an entity.
     *
     * @param player the player who wants to shear
     * @param entity the entity the player intends to shear
     * @return true if the player can shear the object at the location, false otherwise
     */
    boolean canShear(Player player, Entity entity);

    /**
     * Determines whether a player can trample a block at a given location.
     *
     * @param player   the player attempting to trample the block
     * @param location the location of the block to trample
     * @return true if the player can trample the block at the location, false otherwise
     */
    boolean canTrample(Player player, Location location);

    /**
     * Determines whether a player can enter an area.
     *
     * @param player the player who wants to enter the area
     * @param area   the area to enter
     * @return true if the player can enter the area, false otherwise
     */
    boolean canEnter(Player player, Area area);

    /**
     * Determines whether a player can leave an area.
     *
     * @param player the player who wants to leave the area
     * @param area   the area to leave
     * @return true if the player can leave the area, false otherwise
     */
    boolean canLeave(Player player, Area area);
}
