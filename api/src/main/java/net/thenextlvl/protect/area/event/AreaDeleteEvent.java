package net.thenextlvl.protect.area.event;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;

/**
 * This event is triggered when an Area is requested for deletion.
 * Cancelling this event results in the area not being deleted.
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaDeleteEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    public AreaDeleteEvent(RegionizedArea<?> area) {
        super(area);
    }
}
