package net.thenextlvl.protect.area.event.flag;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an abstract event bound to an area and flag
 *
 * @param <T> The type of the flag value.
 */
@Getter
@Setter
@NullMarked
public abstract class AreaFlagEvent<T> extends AreaEvent<Area> implements Cancellable {
    private final Flag<T> flag;
    private boolean cancelled;

    /**
     * Constructs a new AreaFlagEvent with the given area and flag.
     *
     * @param area the area associated with this event
     * @param flag the flag associated with this event
     */
    protected AreaFlagEvent(Area area, Flag<T> flag) {
        super(area);
        this.flag = flag;
    }
}
