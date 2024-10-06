package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that is triggered when a flag is reset in an area.
 * Cancelling this event results in the flag not being reset.
 *
 * @param <T> The type of the flag value.
 */
@Getter
@Setter
public class AreaFlagResetEvent<T> extends AreaFlagEvent<T> {

    public AreaFlagResetEvent(@NotNull Area area, @NotNull Flag<T> flag) {
        super(area, flag);
    }
}
