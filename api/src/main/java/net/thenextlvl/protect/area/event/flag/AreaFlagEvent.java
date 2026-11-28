package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import net.thenextlvl.protect.flag.FlagInstance;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents an abstract event bound to an area and flag
 */
public abstract class AreaFlagEvent extends AreaEvent<Area> implements Cancellable {
    private final FlagInstance<?> flagInstance;
    private boolean cancelled;

    /**
     * Constructs a new AreaFlagEvent with the given area and flag.
     *
     * @param area         the area associated with this event
     * @param flagInstance the flag instance associated with this event
     */
    @ApiStatus.Internal
    protected AreaFlagEvent(final Area area, final FlagInstance<?> flagInstance) {
        super(area);
        this.flagInstance = flagInstance;
    }

    @Contract(pure = true)
    public FlagInstance<?> getFlagInstance() {
        return flagInstance;
    }

    @Override
    @Contract(pure = true)
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    @Contract(mutates = "this")
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
