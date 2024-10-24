package net.thenextlvl.protect.area.event.schematic;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is triggered when a schematic associated with an area is about to be deleted.
 * Cancelling this event results in the schematic not being deleted.
 */
@Getter
@Setter
public class AreaSchematicDeleteEvent extends AreaEvent<@NotNull RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new AreaSchematicDeleteEvent.
     *
     * @param area The regionized area associated with the schematic being deleted.
     */
    @ApiStatus.Internal
    public AreaSchematicDeleteEvent(@NotNull RegionizedArea<?> area) {
        super(area);
    }
}
