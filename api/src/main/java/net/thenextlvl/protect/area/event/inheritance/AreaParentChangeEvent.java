package net.thenextlvl.protect.area.event.inheritance;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The AreaParentChangeEvent class represents an event that occurs when the parent of an area is changed.
 * This event allows for the cancellation of the parent change.
 * <p>
 * Note: Calling {@link RegionizedArea#setParent(Area)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
public class AreaParentChangeEvent extends AreaEvent<@NotNull Area> implements Cancellable {
    private boolean cancelled;
    private @Nullable Area parent;

    /**
     * Constructs a new AreaParentChangeEvent.
     * This event is fired when the parent of an area is changed.
     *
     * @param area   the area whose parent is being changed
     * @param parent the new parent area, or null if the parent is being removed
     */
    @ApiStatus.Internal
    public AreaParentChangeEvent(@NotNull Area area, @Nullable Area parent) {
        super(area);
        this.parent = parent;
    }
}
