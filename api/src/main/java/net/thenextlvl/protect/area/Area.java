package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import net.thenextlvl.protect.flag.FlagProvider;
import org.bukkit.World;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * The Area interface represents an area inside a world.
 */
@MethodsReturnNotNullByDefault
public interface Area extends Container, FlagProvider, Comparable<Area> {
    /**
     * Retrieves the parent area of this area.
     * The parent area inherits its values, such as flags, to this area.
     *
     * @return an Optional containing the parent area if it exists, otherwise an empty Optional
     */
    Optional<Area> getParent();

    /**
     * Retrieves the set of parent areas associated with this area.
     *
     * @return a LinkedHashSet of parent areas.
     */
    LinkedHashSet<Area> getParents();

    /**
     * Retrieves the name of the area.
     *
     * @return the name of the area
     */
    @NamePattern
    String getName();

    /**
     * Retrieves the priority of the area.
     * <p>
     * The priority determines the order in which areas are considered when resolving conflicts or determining containment.
     * A higher priority value indicates a higher priority for the area.
     *
     * @return the priority of the area
     */
    int getPriority();

    /**
     * Sets the priority of the area.
     * The priority determines the order in which areas are considered when resolving conflicts or determining containment.
     * A higher priority value indicates a higher priority for the area.
     *
     * @param priority the priority value to set
     * @return whether the priority was changed
     */
    boolean setPriority(int priority);

    /**
     * Determines whether this area can interact with another area.
     *
     * @param area the area to check interaction capability with
     * @return true if this area can interact with the specified area, false otherwise
     */
    boolean canInteract(Area area);

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
