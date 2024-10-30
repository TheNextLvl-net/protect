package net.thenextlvl.protect.area.event.member;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
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
}
