package net.thenextlvl.protect.area.event.member;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * An event that represents a change in the members of an area.
 * Cancelling this event results in the member not being added.
 * <p>
 * Note: Calling {@link RegionizedArea#addMember(UUID)} within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaMemberAddEvent extends AreaEvent<Area> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private UUID member;
    private boolean cancelled;

    /**
     * Constructs a new AreaMemberAddEvent.
     *
     * @param area   the regionized area to which a member is being added
     * @param member the UUID of the member to be added to the area
     */
    @ApiStatus.Internal
    public AreaMemberAddEvent(Area area, UUID member) {
        super(area);
        this.member = member;
    }

    @Contract(pure = true)
    public UUID getMember() {
        return member;
    }

    @Contract(mutates = "this")
    public void setMember(UUID member) {
        this.member = member;
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
