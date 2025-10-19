package net.thenextlvl.protect.area.event.region;

import com.sk89q.worldedit.regions.Region;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when the region associated with an area is about to be redefined.
 * Cancelling this event results in the region not being redefined.
 *
 * @param <T> The type of region associated with the area of this event
 */
@NullMarked
public final class AreaRedefineEvent<T extends Region> extends AreaEvent<RegionizedArea<T>> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private T region;

    /**
     * Constructs a new AreaRedefineEvent with the specified parameters.
     *
     * @param area   The regionized area associated with this event
     * @param region The region that is to be redefined
     */
    @ApiStatus.Internal
    public AreaRedefineEvent(RegionizedArea<T> area, T region) {
        super(area);
        this.region = region;
    }

    @Contract(pure = true)
    public T getRegion() {
        return region;
    }

    @Contract(mutates = "this")
    public void setRegion(T region) {
        this.region = region;
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
