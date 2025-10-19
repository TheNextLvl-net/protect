package net.thenextlvl.protect.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The AreaProvider interface provides methods to retrieve areas in a world, at a location, or containing a block or entity.
 * It also allows retrieving the global area of the world and areas by their name.
 */
@NullMarked
public interface AreaProvider {
    /**
     * Retrieves a stream of all areas.
     *
     * @return a stream of areas
     */
    @Contract(pure = true)
    Stream<Area> getAreas();

    /**
     * Retrieves a stream of areas in the given world.
     *
     * @param world the world to retrieve areas from
     * @return a stream of areas in the given world
     */
    @Contract(pure = true)
    Stream<Area> getAreas(World world);

    /**
     * Retrieves a stream of areas at the given location.
     *
     * @param location the location to retrieve areas from
     * @return a stream of areas at the given location
     */
    @Contract(pure = true)
    Stream<Area> getAreas(Location location);

    /**
     * Retrieves a stream of areas that contain the given block.
     *
     * @param block the block to check for containment
     * @return a stream of areas containing the given block
     */
    @Contract(pure = true)
    default Stream<Area> getAreas(Block block) {
        return getAreas(block.getLocation());
    }

    /**
     * Retrieves a stream of areas that contain the given entity.
     *
     * @param entity the entity to check for containment
     * @return a stream of areas containing the given entity
     */
    @Contract(pure = true)
    default Stream<Area> getAreas(Entity entity) {
        return getAreas(entity.getLocation());
    }

    /**
     * Retrieves the global area of the given world.
     *
     * @param world the world to retrieve the global area from
     * @return the global area of the given world
     */
    @Contract(pure = true)
    GlobalArea getArea(World world);

    /**
     * Retrieves the area with the highest priority at the given location.
     *
     * @param location the location to retrieve the area from
     * @return the area at the given location
     */
    @Contract(pure = true)
    Area getArea(Location location);

    /**
     * Retrieves the area with the highest priority that contains the given block.
     *
     * @param block the block to retrieve the area from
     * @return the area that contains the given block
     */
    @Contract(pure = true)
    default Area getArea(Block block) {
        return getArea(block.getLocation());
    }

    /**
     * Retrieves the area with the highest priority that contains the given entity.
     *
     * @param entity the entity to check for containment
     * @return the area that contains the given entity
     */
    @Contract(pure = true)
    default Area getArea(Entity entity) {
        return getArea(entity.getLocation());
    }

    /**
     * Retrieves an optional Area with the given name.
     *
     * @param name the name of the Area to retrieve
     * @return an Optional containing the Area if found, or an empty Optional otherwise
     */
    @Contract(pure = true)
    Optional<Area> getArea(String name);
}
