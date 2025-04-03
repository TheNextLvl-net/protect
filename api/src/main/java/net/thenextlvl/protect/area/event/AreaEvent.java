package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

/**
 * This class represents an abstract base class for events related to areas.
 *
 * @param <T> The area type of this event
 */
@NullMarked
public abstract class AreaEvent<T extends Area> extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final T area;

    protected AreaEvent(T area) {
        this(!area.getServer().isPrimaryThread(), area);
    }

    protected AreaEvent(boolean async, T area) {
        super(async);
        this.area = area;
    }

    public T getArea() {
        return area;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
