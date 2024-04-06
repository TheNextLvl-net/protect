package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The AreaProvider interface provides methods to retrieve areas in a world, at a location, or containing a block or entity.
 * It also allows to retrieve the global area of a world and areas by their name.
 */
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface AreaProvider {

    /**
     * Retrieves a stream of all areas.
     *
     * @return a stream of areas
     */
    Stream<Area> getAreas();

    /**
     * Retrieves a stream of areas in the given world.
     *
     * @param world the world to retrieve areas from
     * @return a stream of areas in the given world
     */
    Stream<Area> getAreas(World world);

    /**
     * Retrieves a stream of areas at the given location.
     *
     * @param location the location to retrieve areas from
     * @return a stream of areas at the given location
     */
    Stream<Area> getAreas(Location location);

    /**
     * Retrieves a stream of areas that contain the given block.
     *
     * @param block the block to check for containment
     * @return a stream of areas containing the given block
     */
    default Stream<Area> getAreas(Block block) {
        return getAreas(block.getLocation());
    }

    /**
     * Retrieves a stream of areas that contain the given entity.
     *
     * @param entity the entity to check for containment
     * @return a stream of areas containing the given entity
     */
    default Stream<Area> getAreas(Entity entity) {
        return getAreas(entity.getLocation());
    }

    /**
     * Retrieves the global area of the given world.
     *
     * @param world the world to retrieve the global area from
     * @return the global area of the given world
     */
    GlobalArea getArea(World world);

    /**
     * Retrieves the area with the highest priority at the given location.
     *
     * @param location the location to retrieve the area from
     * @return the area at the given location
     */
    Area getArea(Location location);

    /**
     * Retrieves the area with the highest priority that contains the given block.
     *
     * @param block the block to retrieve the area from
     * @return the area that contains the given block
     */
    default Area getArea(Block block) {
        return getArea(block.getLocation());
    }

    /**
     * Retrieves the area with the highest priority that contains the given entity.
     *
     * @param entity the entity to check for containment
     * @return the area that contains the given entity
     */
    default Area getArea(Entity entity) {
        return getArea(entity.getLocation());
    }

    /**
     * Retrieves an optional Area with the given name.
     *
     * @param name the name of the Area to retrieve
     * @return an Optional containing the Area if found, or an empty Optional otherwise
     */
    Optional<Area> getArea(@NamePattern String name);
}
