package net.thenextlvl.protect.area.event.flag;

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

    public Flag<T> getFlag() {
        return flag;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
