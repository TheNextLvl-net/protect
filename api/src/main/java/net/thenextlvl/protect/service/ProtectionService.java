package net.thenextlvl.protect.service;

import core.annotation.ParametersAreNotNullByDefault;
import net.thenextlvl.protect.area.Area;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * The ProtectionService interface provides methods to determine whether a player
 * is permitted to perform certain actions in certain locations or areas.
 */
@ParametersAreNotNullByDefault
public interface ProtectionService {

    /**
     * Determines whether a player can build at a given location.
     *
     * @param player   the player who wants to build
     * @param location the location where the player intends to build
     * @return true if the player can build at the location, false otherwise
     */
    boolean canBuild(Player player, Location location);

    /**
     * Determines whether a player can break a block at a given location.
     *
     * @param player   the player who wants to break the block
     * @param location the location of the block the player intends to break
     * @return true if the player can break the block at the location, false otherwise
     */
    boolean canBreak(Player player, Location location);

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
