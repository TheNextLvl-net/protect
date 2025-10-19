package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when a schematic associated with an area is about to be deleted.
 * Cancelling this event results in the schematic not being deleted.
 */
@NullMarked
public final class AreaSchematicDeleteEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    /**
     * Constructs a new AreaSchematicDeleteEvent.
     *
     * @param area The regionized area associated with the schematic being deleted.
     */
    @ApiStatus.Internal
    public AreaSchematicDeleteEvent(RegionizedArea<?> area) {
        super(area);
    }

    @Override
    @Contract(pure = true)
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    @Contract(mutates = "this")
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    @Contract(pure = true)
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
