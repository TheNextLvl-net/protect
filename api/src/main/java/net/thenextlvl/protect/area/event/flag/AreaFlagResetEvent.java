package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * Represents an event triggered when a flag is reset in an area.
 * Cancelling this event results in the flag not being reset.
 *
 * @param <T> The type of the flag value.
 */
public final class AreaFlagResetEvent<T> extends AreaFlagEvent<T> {
    private static final @NonNull HandlerList handlerList = new HandlerList();

    /**
     * Constructs a new AreaFlagResetEvent with the specified area and flag.
     *
     * @param area the area associated with this event
     * @param flag the flag associated with this event
     */
    @ApiStatus.Internal
    public AreaFlagResetEvent(@NonNull Area area, @NonNull Flag<T> flag) {
        super(area, flag);
    }

    @Override
    @Contract(pure = true)
    public @NonNull HandlerList getHandlers() {
        return handlerList;
    }

    @Contract(pure = true)
    public static @NonNull HandlerList getHandlerList() {
        return handlerList;
    }
}
