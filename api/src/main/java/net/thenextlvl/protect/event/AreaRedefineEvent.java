package net.thenextlvl.protect.event;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;

/**
 * This event is fired when the region associated with an area is about to be redefined.
 * Cancelling this event results in the region not being redefined.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
public class AreaRedefineEvent<T extends Region> extends AreaEvent implements Cancellable {
    private final T region;
    private boolean cancelled;

    public AreaRedefineEvent(RegionizedArea<T> area, T region) {
        super(area);
        this.region = region;
    }
}
