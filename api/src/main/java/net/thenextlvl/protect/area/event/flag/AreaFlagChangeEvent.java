package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;

/**
 * Represents an event that occurs when a flag in an area is changed.
 * Cancelling this event results in the flag not being changed.
 *
 * @param <T> The type of the flag value.
 */
public class AreaFlagChangeEvent<T> extends AreaFlagEvent<T> {
    private T newState;

    /**
     * Constructs a new AreaFlagChangeEvent, which represents the change of a flag within an area.
     *
     * @param area     The area associated with this event.
     * @param flag     The flag that is being changed.
     * @param newState The new state of the flag.
     */
    public AreaFlagChangeEvent(@NonNull Area area, @NonNull Flag<T> flag, T newState) {
        super(area, flag);
        this.newState = newState;
    }

    public T getNewState() {
        return newState;
    }

    public void setNewState(T newState) {
        this.newState = newState;
    }
}
