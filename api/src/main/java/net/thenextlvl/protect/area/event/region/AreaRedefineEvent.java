package net.thenextlvl.protect.area.event.region;

import com.sk89q.worldedit.regions.Region;
import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;

/**
 * This event is fired when the region associated with an area is about to be redefined.
 * Cancelling this event results in the region not being redefined.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaRedefineEvent<T extends Region> extends AreaEvent<RegionizedArea<T>> implements Cancellable {
    private boolean cancelled;
    private final T region;

    public AreaRedefineEvent(RegionizedArea<T> area, T region) {
        super(area);
        this.region = region;
    }
}
