package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Cancellable;

/**
 * This event is triggered when an Area is requested for deletion.
 * Cancelling this event results in the area not being deleted.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
public class AreaDeleteEvent extends AreaEvent implements Cancellable {
    private boolean cancelled;

    public AreaDeleteEvent(Area area) {
        super(area);
    }
}
