package net.thenextlvl.protect.area.event.member;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * An event that represents a change in the owner of an area.
 * Cancelling this event results in the owner not being changed.
 * <p>
 * Note: Calling {@link RegionizedArea#setOwner(UUID)} within this event can lead to an infinite loop.
 *
 * @param <T> The type of the area associated with this event
 */
@Getter
@Setter
public class AreaOwnerChangeEvent extends AreaEvent<@NotNull Area> implements Cancellable {
    private @Nullable UUID owner;
    private boolean cancelled;

    /**
     * Constructs a new AreaOwnerChangeEvent.
     *
     * @param area  The area for which the ownership change event is being created
     * @param owner The new owner's UUID. Can be null to indicate no owner
     */
    @ApiStatus.Internal
    public AreaOwnerChangeEvent(@NotNull Area area, @Nullable UUID owner) {
        super(area);
        this.owner = owner;
    }
}
