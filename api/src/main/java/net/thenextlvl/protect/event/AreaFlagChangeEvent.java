package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a flag in an area is changed.
 * Cancelling this event results in the flag not being changed.
 *
 * @param <T> The type of the flag value.
 */
@Getter
@Setter
public class AreaFlagChangeEvent<T> extends AreaFlagEvent<T> {
    private T newState;

    public AreaFlagChangeEvent(@NotNull Area area, @NotNull Flag<T> flag, T newState) {
        super(area, flag);
        this.newState = newState;
    }
}
