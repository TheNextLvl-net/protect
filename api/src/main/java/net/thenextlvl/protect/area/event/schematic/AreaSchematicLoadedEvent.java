package net.thenextlvl.protect.area.event.schematic;

import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when an area's schematic has been successfully loaded.
 * It extends the {@link AreaEvent} class and provides the associated {@link RegionizedArea}.
 */
@NullMarked
public final class AreaSchematicLoadedEvent extends AreaEvent<RegionizedArea<?>> {
    private static final HandlerList handlerList = new HandlerList();

    /**
     * This event is triggered when an area's schematic has been successfully loaded.
     *
     * @param area The regionized area whose schematic has been loaded.
     */
    @ApiStatus.Internal
    public AreaSchematicLoadedEvent(RegionizedArea<?> area) {
        super(area);
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
