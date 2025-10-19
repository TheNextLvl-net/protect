package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when a new Area is created.
 * <p>
 * Note: Calling {@link AreaCreator#create()} or triggering a
 * {@link org.bukkit.event.world.WorldInitEvent WorldInitEvent}
 * within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaCreateEvent extends AreaEvent<Area> {
    private static final HandlerList handlerList = new HandlerList();

    /**
     * This event is fired when a new Area is created.
     *
     * @param area the area that has been created
     */
    @ApiStatus.Internal
    public AreaCreateEvent(Area area) {
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
