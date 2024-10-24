package net.thenextlvl.protect.area.event.member;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;

import java.util.Set;
import java.util.UUID;

/**
 * An event that represents a change in the members of an area.
 * Cancelling this event results in the members not being set.
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaMembersChangeEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private Set<UUID> members;
    private boolean cancelled;

    public AreaMembersChangeEvent(RegionizedArea<?> area, Set<UUID> members) {
        super(area);
        this.members = members;
    }
}
