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
    boolean contains(Block block);

    /**
     * Determines if the given entity is contained within the container.
     *
     * @param entity the entity to check
     * @return true if the entity is contained within the container, false otherwise
     */
    boolean contains(Entity entity);

    /**
     * Retrieves a list of entities that are contained within this container.
     *
     * @return a list of entities
     */
    List<Entity> getEntities();

    /**
     * Retrieves a list of players that are contained within this container.
     *
     * @return a list of players
     */
    List<Player> getPlayers();
}
