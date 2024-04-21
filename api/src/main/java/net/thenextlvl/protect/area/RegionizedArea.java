package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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

    @Override
    @NamePattern.Regionized
    String getName();

    /**
     * Retrieves the region associated with this area.
     *
     * @return The region associated with this area.
     */
    T getRegion();

    /**
     * Sets the region associated with this {@link RegionizedArea}.
     *
     * @param region the region to set
     * @return true if the region was successfully set, false otherwise
     */
    boolean setRegion(T region);

    /**
     * Retrieves the UUID of the owner of the Area.
     *
     * @return the UUID of the owner
     */
    @Nullable
    UUID getOwner();

    /**
     * Checks if the area has an owner.
     *
     * @return true if the area has an owner, false otherwise
     */
    boolean hasOwner();

    /**
     * Sets the owner of the Area object.
     *
     * @param owner the UUID of the owner to set
     */
    void setOwner(@Nullable UUID owner);

    /**
     * Checks if the area is too big.
     *
     * @return true if the area is too big, false otherwise
     */
    boolean isTooBig();
}
