package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Cancellable;

/**
 * This event is triggered when a schematic associated with an area is about to be deleted.
 * Cancelling this event result in the schematic not being deleted.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
public class AreaSchematicDeleteEvent extends AreaEvent implements Cancellable {
    private boolean cancelled;

    public AreaSchematicDeleteEvent(Area area) {
        super(area);
    }
}
