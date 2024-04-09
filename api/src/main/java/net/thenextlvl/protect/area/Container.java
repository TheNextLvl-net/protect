package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The Container interface represents a container that can contain entities and blocks.
 */
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface Container {

    /**
     * Determines if the given location is contained within the container.
     *
     * @param location the location to check
     * @return true if the location is contained within the container, false otherwise
     */
    boolean contains(Location location);

    /**
     * Determines if the given block is contained within the container.
     *
     * @param block the block to check
     * @return true if the block is contained within the container, false otherwise
     */
    default boolean contains(Block block) {
        return contains(block.getLocation());
    }

    /**
     * Determines if the given entity is contained within the container.
     *
     * @param entity the entity to check
     * @return true if the entity is contained within the container, false otherwise
     */
    default boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }

    /**
     * Retrieves a list of entities that are contained within this container.
     *
     * @return a list of entities
     * @see Container#getHighestEntities()
     */
    List<Entity> getEntities();

    /**
     * Retrieves a list of players that are contained within this container.
     *
     * @return a list of players
     * @see Container#getHighestPlayers()
     */
    List<Player> getPlayers();

    /**
     * Retrieves a list of entities that are contained within this container.
     * This method returns only those entities who's highest-area is equal to this one.
     *
     * @return a List of Entity objects that are contained within this container
     * @see Area#getPriority()
     * @see Container#getEntities()
     */
    List<Entity> getHighestEntities();

    /**
     * Retrieves a list of players that are contained within this container.
     * This method returns only those players who's highest-area is equal to this one.
     *
     * @return a List of Player objects that are contained within this container
     * @see Area#getPriority()
     * @see Container#getPlayers()
     */
    List<Player> getHighestPlayers();
}
