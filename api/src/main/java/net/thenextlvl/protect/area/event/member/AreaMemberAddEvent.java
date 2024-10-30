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
 * Cancelling this event results in the member not being added.
 * <p>
 * Note: Calling {@link RegionizedArea#addMember(UUID)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
@NullMarked
public class AreaMemberAddEvent extends AreaEvent<Area> implements Cancellable {
    private UUID member;
    private boolean cancelled;

    /**
     * Constructs a new AreaMemberAddEvent.
     *
     * @param area   the regionized area to which a member is being added
     * @param member the UUID of the member to be added to the area
     */
    public AreaMemberAddEvent(Area area, UUID member) {
        super(area);
        this.member = member;
    }
}
