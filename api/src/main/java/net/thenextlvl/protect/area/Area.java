package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import net.thenextlvl.protect.flag.FlagProvider;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

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
     */
    void setPriority(int priority);

    /**
     * Retrieves the world associated with this Area.
     *
     * @return the world associated with this Area
     */
    World getWorld();

    /**
     * {@inheritDoc}
     * <p>
     * Compares this Area object with the specified Area object for order.
     * The comparison is based on the priority of the areas.
     *
     * @param area the Area object to be compared
     * @return a negative or positive integer or zero
     */
    @Override
    default int compareTo(@NotNull Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }
}
