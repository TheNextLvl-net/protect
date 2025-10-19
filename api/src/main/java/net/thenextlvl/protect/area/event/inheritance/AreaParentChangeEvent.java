package net.thenextlvl.protect.area.event.inheritance;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The AreaParentChangeEvent class represents an event that occurs when the parent of an area is changed.
 * This event allows for the cancellation of the parent change.
 * <p>
 * Note: Calling {@link RegionizedArea#setParent(Area)} within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaParentChangeEvent extends AreaEvent<Area> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private @Nullable Area parent;

    /**
     * Constructs a new AreaParentChangeEvent.
     * This event is fired when the parent of an area is changed.
     *
     * @param area   the area whose parent is being changed
     * @param parent the new parent area, or null if the parent is being removed
     */
    @ApiStatus.Internal
    public AreaParentChangeEvent(Area area, @Nullable Area parent) {
        super(area);
        this.parent = parent;
    }

    @Contract(pure = true)
    public @Nullable Area getParent() {
        return parent;
    }

    @Contract(mutates = "this")
    public void setParent(@Nullable Area parent) {
        this.parent = parent;
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
