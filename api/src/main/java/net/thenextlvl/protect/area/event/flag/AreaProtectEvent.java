package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an event triggered by the /area protect command.
 * This event occurs when an area protection command is issued, signaling
 * that a specific area is to be protected. Users can listen to this event
 * to perform actions related to area protection.
 */
@NullMarked
public final class AreaProtectEvent extends AreaEvent<Area> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    /**
     * Constructs an AreaProtectEvent.
     *
     * @param area The Area to be protected
     */
    @ApiStatus.Internal
    public AreaProtectEvent(Area area) {
        super(area);
    }

    @Override
    @Contract(pure = true)
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    @Contract(mutates = "this")
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
