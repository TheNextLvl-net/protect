package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event triggered when a flag is reset in an area.
 * Cancelling this event results in the flag not being reset.
 *
 * @param <T> The type of the flag value.
 */
public class AreaFlagResetEvent<T> extends AreaFlagEvent<T> {
    /**
     * Constructs a new AreaFlagResetEvent with the specified area and flag.
     *
     * @param area the area associated with this event
     * @param flag the flag associated with this event
     */
    @ApiStatus.Internal
    public AreaFlagResetEvent(@NotNull Area area, @NotNull Flag<T> flag) {
        super(area, flag);
    }
}
