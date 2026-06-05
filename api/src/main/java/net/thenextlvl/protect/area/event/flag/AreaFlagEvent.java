package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents an abstract event bound to an area and flag
 *
 */
public abstract class AreaFlagEvent extends AreaEvent<Area> implements Cancellable {
    private final Flag flag;
    private boolean cancelled;

    /**
     * Constructs a new AreaFlagEvent with the given area and flag.
     *
     * @param area the area associated with this event
     * @param flag the flag associated with this event
     */
    @ApiStatus.Internal
    protected AreaFlagEvent(final Area area, final Flag flag) {
        super(area);
        this.flag = flag;
    }

    @Contract(pure = true)
    public Flag getFlag() {
        return flag;
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
