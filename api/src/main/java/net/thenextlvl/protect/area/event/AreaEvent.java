package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This class represents an abstract base class for events related to areas.
 *
 * @param <T> The area type of this event
 */
@NullMarked
public abstract class AreaEvent<T extends Area> extends Event {
    private final T area;

    @ApiStatus.Internal
    protected AreaEvent(T area) {
        this(!area.getServer().isPrimaryThread(), area);
    }

    @ApiStatus.Internal
    protected AreaEvent(boolean async, T area) {
        super(async);
        this.area = area;
    }

    @Contract(pure = true)
    public T getArea() {
        return area;
    }
}
