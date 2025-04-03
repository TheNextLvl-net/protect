package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when a schematic of a regionized area is loaded.
 * This event is cancellable, meaning it can be prevented from proceeding.
 */
@NullMarked
public class AreaSchematicLoadEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new AreaSchematicLoadEvent.
     *
     * @param area The regionized area whose schematic is being loaded.
     */
    public AreaSchematicLoadEvent(RegionizedArea<?> area) {
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
