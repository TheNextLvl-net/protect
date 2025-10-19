package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.AreaService;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when an Area is requested for deletion.
 * Cancelling this event results in the area not being deleted.
 * <p>
 * Note: Calling {@link AreaService#delete(RegionizedArea)} within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaDeleteEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    /**
     * Constructs an AreaDeleteEvent.
     *
     * @param area the RegionizedArea that is requested to be deleted
     */
    @ApiStatus.Internal
    public AreaDeleteEvent(RegionizedArea<?> area) {
        super(area);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
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
