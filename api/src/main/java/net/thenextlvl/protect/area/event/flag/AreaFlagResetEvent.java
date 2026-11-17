package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.FlagInstance;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents an event triggered when a flag is reset in an area.
 * Cancelling this event results in the flag not being reset.
 */
public final class AreaFlagResetEvent extends AreaFlagEvent {
    private static final HandlerList handlerList = new HandlerList();

    /**
     * Constructs a new AreaFlagResetEvent with the specified area and flag.
     *
     * @param area the area associated with this event
     * @param flag the flag associated with this event
     */
    @ApiStatus.Internal
    public AreaFlagResetEvent(final Area area, final FlagInstance<?> flag) {
        super(area, flag);
    }

    @Override
    @Contract(pure = true)
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
