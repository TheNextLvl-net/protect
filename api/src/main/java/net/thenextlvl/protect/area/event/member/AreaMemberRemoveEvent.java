package net.thenextlvl.protect.area.event.member;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;

import java.util.UUID;

/**
 * An event that represents a change in the members of an area.
 * Cancelling this event results in the member not being removed.
 *
 * @param <T> The type of the area associated with this event
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaMemberRemoveEvent<T extends RegionizedArea<?>> extends AreaEvent<T> implements Cancellable {
    private UUID member;
    private boolean cancelled;

    public AreaMemberRemoveEvent(T area, UUID member) {
        super(area);
        this.member = member;
    }
}
