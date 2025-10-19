package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when a schematic of a regionized area is loaded.
 * This event is cancellable, meaning it can be prevented from proceeding.
 */
@NullMarked
public final class AreaSchematicLoadEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    /**
     * Constructs a new AreaSchematicLoadEvent.
     *
     * @param area The regionized area whose schematic is being loaded.
     */
    @ApiStatus.Internal
    public AreaSchematicLoadEvent(RegionizedArea<?> area) {
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
