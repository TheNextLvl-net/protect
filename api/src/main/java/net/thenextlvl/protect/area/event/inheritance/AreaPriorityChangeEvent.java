package net.thenextlvl.protect.area.event.inheritance;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an event that occurs when the priority of an area is changed.
 * This event is fired to inform listeners about the change in the priority of a particular area.
 * <p>
 * Note: Calling {@link Area#setPriority(int)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
@NullMarked
public class AreaPriorityChangeEvent extends AreaEvent<Area> implements Cancellable {
    private boolean cancelled;
    private int priority;

    /**
     * This event is fired when the priority of an area is changed.
     *
     * @param area     The area whose priority is being changed
     * @param priority The new priority value
     */
    public AreaPriorityChangeEvent(Area area, int priority) {
        super(area);
        this.priority = priority;
    }
}
