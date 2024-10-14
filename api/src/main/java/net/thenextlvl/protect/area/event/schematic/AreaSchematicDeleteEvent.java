package net.thenextlvl.protect.area.event.schematic;

import com.sk89q.worldedit.regions.Region;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;

/**
 * This event is triggered when a schematic associated with an area is about to be deleted.
 * Cancelling this event result in the schematic not being deleted.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaSchematicDeleteEvent<T extends Region> extends AreaEvent<RegionizedArea<T>> implements Cancellable {
    private boolean cancelled;

    public AreaSchematicDeleteEvent(RegionizedArea<T> area) {
        super(area);
    }
}
