package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is triggered when an area's schematic has been successfully loaded.
 * It extends the {@link AreaEvent} class and provides the associated {@link RegionizedArea}.
 */
public class AreaSchematicLoadedEvent extends AreaEvent<@NotNull RegionizedArea<?>> {
    /**
     * This event is triggered when an area's schematic has been successfully loaded.
     *
     * @param area The regionized area whose schematic has been loaded.
     */
    @ApiStatus.Internal
    public AreaSchematicLoadedEvent(@NotNull RegionizedArea<?> area) {
        super(area);
    }
}
