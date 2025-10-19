package net.thenextlvl.protect.area.event.member;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * An event that represents a change in the owner of an area.
 * Cancelling this event results in the owner not being changed.
 * <p>
 * Note: Calling {@link RegionizedArea#setOwner(UUID)} within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaOwnerChangeEvent extends AreaEvent<Area> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private @Nullable UUID owner;
    private boolean cancelled;

    /**
     * Constructs a new AreaOwnerChangeEvent.
     *
     * @param area  The area for which the ownership change event is being created
     * @param owner The new owner's UUID. Can be null to indicate no owner
     */
    @ApiStatus.Internal
    public AreaOwnerChangeEvent(Area area, @Nullable UUID owner) {
        super(area);
        this.owner = owner;
    }

    @Contract(pure = true)
    public @Nullable UUID getOwner() {
        return owner;
    }

    @Contract(mutates = "this")
    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
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
