package net.thenextlvl.protect.area;

import net.thenextlvl.protect.flag.FlagProvider;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The Area interface represents an area inside a world.
 */
@NullMarked
public interface Area extends Container, FlagProvider, Comparable<Area> {
    /**
     * Retrieves the set of parent areas associated with this area.
     *
     * @return a LinkedHashSet of parent areas.
     */
    LinkedHashSet<Area> getParents();

    /**
     * Retrieves the parent area of this area.
     * The parent area inherits its values, such as flags, to this area.
     *
     * @return an Optional containing the parent area if it exists, otherwise an empty Optional
     */
    Optional<Area> getParent();

    /**
     * Retrieves the owner of the regionized area.
     *
     * @return an Optional containing the UUID of the owner, or empty
     */
    Optional<UUID> getOwner();

    /**
     * Retrieves the server associated with this area.
     *
     * @return the server associated with this area
     */
    Server getServer();

    /**
     * Retrieves the set of UUIDs representing the members of the regionized area.
     *
     * @return a set of UUIDs representing the members.
     */
    Set<UUID> getMembers();

    /**
     * Retrieves the name of the area.
     *
     * @return the name of the area
     */
    String getName();

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

    /**
     * Adds a member to the regionized area.
     *
     * @param uuid the UUID of the member to add
     * @return whether the member was added
     */
    boolean addMember(UUID uuid);

    /**
     * Determines whether this area can interact with another area.
     *
     * @param area the area to check interaction capability with
     * @return true if this area can interact with the specified area, false otherwise
     */
    boolean canInteract(Area area);

    /**
     * Checks if a given UUID is a member of the regionized area.
     *
     * @param uuid the UUID to check
     * @return true if the UUID is a member, false otherwise
     */
    boolean isMember(UUID uuid);

    /**
     * Checks if a given UUID is either the owner or a member of the regionized area.
     *
     * @param uuid the UUID to check
     * @return true if the UUID is either the owner or a member, false otherwise
     */
    boolean isPermitted(UUID uuid);

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
     * Sets the priority of the area.
     * The priority determines the order in which areas are considered when resolving conflicts or determining containment.
     * A higher priority value indicates a higher priority for the area.
     *
     * @param priority the priority value to set
     * @return whether the priority was changed
     */
    boolean setPriority(int priority);

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
     * Sets the members of the regionized area.
     *
     * @param members a set of UUIDs representing the new members to set
     */
    void setMembers(Set<UUID> members);
}
