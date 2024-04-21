package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import net.thenextlvl.protect.flag.FlagProvider;
import org.bukkit.World;

import java.io.File;

/**
 * The Area interface represents an area inside a world.
 */
@MethodsReturnNotNullByDefault
public interface Area extends Container, FlagProvider, Comparable<Area> {
    /**
     * Retrieves the name of the area.
     *
     * @return the name of the area
     */
    @NamePattern
    String getName();

    /**
     * Retrieves the priority of the area.
     * The priority determines the order in which areas are considered when resolving conflicts or determining containment.
     * A higher priority value indicates a higher priority for the area.
     * <p>
     * Note: This method does not validate the uniqueness of the priority value within the same container.
     *
     * @return the priority of the area
     */
    int getPriority();

    /**
     * Sets the priority of the area.
     * The priority determines the order in which areas are considered when resolving conflicts or determining containment.
     * A higher priority value indicates a higher priority for the area.
     * <p>
     * Note: This method does not validate the uniqueness of the priority value within the same container.
     *
     * @param priority the priority value to set
     * @return whether the priority was changed
     */
    boolean setPriority(int priority);

    /**
     * Retrieves the world associated with this Area.
     *
     * @return the world associated with this Area
     */
    World getWorld();

    /**
     * Retrieves the data folder associated with this object.
     *
     * @return the data folder associated with this object
     */
    File getDataFolder();

    /**
     * Retrieves the File associated with this object.
     *
     * @return the File associated with this object
     */
    File getFile();
}
