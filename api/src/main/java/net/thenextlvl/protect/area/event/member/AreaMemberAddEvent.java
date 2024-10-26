package net.thenextlvl.protect.area.event.member;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * An event that represents a change in the members of an area.
 * Cancelling this event results in the member not being added.
 * <p>
 * Note: Calling {@link RegionizedArea#addMember(UUID)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
public class AreaMemberAddEvent extends AreaEvent<@NotNull Area> implements Cancellable {
    private @NotNull UUID member;
    private boolean cancelled;

    /**
     * Constructs a new AreaMemberAddEvent.
     *
     * @param area  the regionized area to which a member is being added
     * @param member the UUID of the member to be added to the area
     */
    @ApiStatus.Internal
    public AreaMemberAddEvent(@NotNull Area area, @NotNull UUID member) {
        super(area);
        this.member = member;
    }
}
