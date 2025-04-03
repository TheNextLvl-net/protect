package net.thenextlvl.protect.area.event.member;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;


/**
 * An event that represents a change in the members of an area.
 * Cancelling this event results in the member not being removed.
 * <p>
 * Note: Calling {@link RegionizedArea#removeMember(UUID)} within this event can lead to an infinite loop.
 */
@NullMarked
public class AreaMemberRemoveEvent extends AreaEvent<Area> implements Cancellable {
    private UUID member;
    private boolean cancelled;

    /**
     * Constructs a new AreaMemberRemoveEvent.
     *
     * @param area   The regionized area from which a member is being removed.
     * @param member The UUID of the member being removed.
     */
    public AreaMemberRemoveEvent(Area area, UUID member) {
        super(area);
        this.member = member;
    }

    public UUID getMember() {
        return member;
    }

    public void setMember(UUID member) {
        this.member = member;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
