package net.thenextlvl.protect.area.event.inheritance;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when the parent of an area is changed.
 */
@Getter
@Setter
public class AreaParentChangeEvent extends AreaEvent<@NotNull Area> implements Cancellable {
    private boolean cancelled;
    private @Nullable Area parent;

    public AreaParentChangeEvent(@NotNull Area area, @Nullable Area parent) {
        super(area);
        this.parent = parent;
    }
}
