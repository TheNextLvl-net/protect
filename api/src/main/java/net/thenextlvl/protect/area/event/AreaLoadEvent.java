package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when an Area is loaded.
 * <p>
 * Note: Calling {@link AreaCreator#create()} within this event can lead to an infinite loop.
 */
@NullMarked
public final class AreaLoadEvent extends AreaEvent<Area> {
    private static final HandlerList handlerList = new HandlerList();

    /**
     * Constructs a new AreaLoadEvent.
     *
     * @param area the area that has been loaded
     */
    @ApiStatus.Internal
    public AreaLoadEvent(Area area) {
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
