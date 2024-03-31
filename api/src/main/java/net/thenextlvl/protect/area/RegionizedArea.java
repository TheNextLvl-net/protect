package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.schematic.SchematicHolder;

/**
 * The RegionizedArea interface represents an area that is bound to a region.
 * It extends the Area interface and the SchematicHolder interface.
 *
 * @param <T> The type of region associated with this area.
 */
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface RegionizedArea<T extends Region> extends Area, SchematicHolder {

    /**
     * Retrieves the region associated with this area.
     *
     * @return The region associated with this area.
     */
    T getRegion();

    /**
     * Redefines the region associated with this area.
     *
     * @param region the new region to redefine
     * @return true if the redefinition is successful, false otherwise
     */
    boolean redefine(T region);

    /**
     * Checks if the area is too big.
     *
     * @return true if the area is too big, false otherwise
     */
    boolean isTooBig();
}
