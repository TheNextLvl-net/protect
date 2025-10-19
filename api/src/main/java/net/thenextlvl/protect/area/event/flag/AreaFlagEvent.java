package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * Represents an abstract event bound to an area and flag
 *
 * @param <T> The type of the flag value.
 */
public abstract class AreaFlagEvent<T> extends AreaEvent<@NonNull Area> implements Cancellable {
    private final @NonNull Flag<T> flag;
    private boolean cancelled;

    /**
     * Constructs a new AreaFlagEvent with the given area and flag.
     *
     * @param area the area associated with this event
     * @param flag the flag associated with this event
     */
    @ApiStatus.Internal
    protected AreaFlagEvent(@NonNull Area area, @NonNull Flag<T> flag) {
        super(area);
        this.flag = flag;
    }

    @Contract(pure = true)
    public @NonNull Flag<T> getFlag() {
        return flag;
    }

    @Override
    @Contract(pure = true)
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    @Contract(mutates = "this")
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
