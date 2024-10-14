package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
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
     * Retrieves the RegionSelector associated with this RegionizedArea.
     *
     * @return The RegionSelector associated with this RegionizedArea.
     */
    RegionSelector getRegionSelector();

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
     * Retrieves the set of UUIDs representing the members of the regionized area.
     *
     * @return a set of UUIDs representing the members.
     */
    @Unmodifiable
    Set<UUID> getMembers();

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
     * Checks if a given UUID is a member of the regionized area.
     *
     * @param uuid the UUID to check
     * @return true if the UUID is a member, false otherwise
     */
    boolean isMember(UUID uuid);

    /**
     * Adds a member to the regionized area.
     *
     * @param uuid the UUID of the member to add
     * @return whether the member was added
     */
    boolean addMember(UUID uuid);

    /**
     * Removes a member from the regionized area.
     *
     * @param uuid the UUID of the member to remove
     * @return whether the member was removed
     */
    boolean removeMember(UUID uuid);

    /**
     * Sets the owner of the Area object.
     *
     * @param owner the UUID of the owner to set
     * @return whether the owner was changed
     */
    boolean setOwner(@Nullable UUID owner);

    /**
     * Checks if the area is too big.
     *
     * @return true if the area is too big, false otherwise
     */
    boolean isTooBig();
}
