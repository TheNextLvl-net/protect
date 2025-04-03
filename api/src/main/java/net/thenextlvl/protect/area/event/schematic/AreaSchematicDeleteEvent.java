package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when a schematic associated with an area is about to be deleted.
 * Cancelling this event results in the schematic not being deleted.
 */
@NullMarked
public class AreaSchematicDeleteEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new AreaSchematicDeleteEvent.
     *
     * @param area The regionized area associated with the schematic being deleted.
     */
    public AreaSchematicDeleteEvent(RegionizedArea<?> area) {
        super(area);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
