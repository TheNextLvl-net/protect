package net.thenextlvl.protect.area.event.inheritance;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an event that occurs when the priority of an area is changed.
 * This event is fired to inform listeners about the change in the priority of a particular area.
 * <p>
 * Note: Calling {@link Area#setPriority(int)} within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaPriorityChangeEvent extends AreaEvent<Area> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private int priority;

    /**
     * This event is fired when the priority of an area is changed.
     *
     * @param area     The area whose priority is being changed
     * @param priority The new priority value
     */
    @ApiStatus.Internal
    public AreaPriorityChangeEvent(Area area, int priority) {
        super(area);
        this.priority = priority;
    }

    @Contract(pure = true)
    public int getPriority() {
        return priority;
    }

    @Contract(mutates = "this")
    public void setPriority(int priority) {
        this.priority = priority;
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
