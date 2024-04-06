package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;

import java.util.UUID;

/**
 * An event that represents a change in the owner of an area.
 * Cancelling this event results in the owner not being changed.
 *
 * @param <T> The type of the area associated with this event
 */
@Getter
@Setter
public class AreaOwnerChangeEvent<T extends RegionizedArea<?>> extends AreaEvent<T> implements Cancellable {
    private boolean cancelled;
    private UUID newOwner;

    public AreaOwnerChangeEvent(T area, UUID newOwner) {
        super(area);
        this.newOwner = newOwner;
    }
}
