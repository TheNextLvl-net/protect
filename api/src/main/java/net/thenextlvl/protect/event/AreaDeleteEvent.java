package net.thenextlvl.protect.event;

import com.sk89q.worldedit.regions.Region;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;

/**
 * This event is triggered when an Area is requested for deletion.
 * Cancelling this event results in the area not being deleted.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaDeleteEvent<T extends Region> extends AreaEvent<RegionizedArea<T>> implements Cancellable {
    private boolean cancelled;

    public AreaDeleteEvent(RegionizedArea<T> area) {
        super(area);
    }
}
