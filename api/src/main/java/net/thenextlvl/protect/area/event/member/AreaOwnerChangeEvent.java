package net.thenextlvl.protect.area.event.member;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * An event that represents a change in the owner of an area.
 * Cancelling this event results in the owner not being changed.
 * <p>
 * Note: Calling {@link RegionizedArea#setOwner(UUID)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
@NullMarked
public class AreaOwnerChangeEvent extends AreaEvent<Area> implements Cancellable {
    private @Nullable UUID owner;
    private boolean cancelled;

    /**
     * Constructs a new AreaOwnerChangeEvent.
     *
     * @param area  The area for which the ownership change event is being created
     * @param owner The new owner's UUID. Can be null to indicate no owner
     */
    public AreaOwnerChangeEvent(Area area, @Nullable UUID owner) {
        super(area);
        this.owner = owner;
    }
}
