package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import net.thenextlvl.protect.exception.CircularInheritanceException;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The RegionizedArea interface represents an area bound to a region.
 * It extends the Area interface and the SchematicHolder interface.
 *
 * @param <T> The type of region associated with this area.
 */
@NullMarked
public interface RegionizedArea<T extends Region> extends Area, SchematicHolder {
    /**
     * Calculates the location of this area.
     *
     * @return the location of this area.
     */
    @Contract(value = " -> new", pure = true)
    Location getLocation();

    /**
     * Retrieves the RegionSelector associated with this RegionizedArea.
     *
     * @return The RegionSelector associated with this RegionizedArea.
     */
    @Contract(value = " -> new", pure = true)
    RegionSelector getRegionSelector();

    /**
     * Retrieves the region associated with this area.
     *
     * @return The region associated with this area.
     */
    @Contract(pure = true)
    T getRegion();

    /**
     * Sets the parent area of this area.
     * The parent area inherits its values, such as flags, to this area.
     *
     * @param parent the parent area to set, or null to clear the parent area
     * @return true if the parent area was successfully set, false otherwise
     * @throws CircularInheritanceException if the parent is this area or contained in its inherited parents
     */
    @Contract(mutates = "this")
    boolean setParent(@Nullable Area parent) throws CircularInheritanceException;

    /**
     * Sets the region associated with this {@link RegionizedArea}.
     *
     * @param region the region to set
     * @return true if the region was successfully set, false otherwise
     */
    @Contract(mutates = "this")
    boolean setRegion(T region);

    /**
     * Checks if the area is too big.
     *
     * @return true if the area is too big, false otherwise
     */
    @Contract(pure = true)
    boolean isTooBig();
}
