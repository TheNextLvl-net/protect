package net.thenextlvl.protect.area.event.inheritance;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the priority of an area is changed.
 */
@Getter
@Setter
public class AreaPriorityChangeEvent extends AreaEvent<@NotNull Area> implements Cancellable {
    private boolean cancelled;
    private int priority;

    public AreaPriorityChangeEvent(@NotNull Area area, int priority) {
        super(area);
        this.priority = priority;
    }
}
