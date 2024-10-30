package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when an area's schematic has been successfully loaded.
 * It extends the {@link AreaEvent} class and provides the associated {@link RegionizedArea}.
 */
@NullMarked
public class AreaSchematicLoadedEvent extends AreaEvent<RegionizedArea<?>> {
    /**
     * This event is triggered when an area's schematic has been successfully loaded.
     *
     * @param area The regionized area whose schematic has been loaded.
     */
    public AreaSchematicLoadedEvent(RegionizedArea<?> area) {
        super(area);
    }
}
