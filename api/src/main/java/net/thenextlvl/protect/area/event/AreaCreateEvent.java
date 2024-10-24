package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a new Area is created.
 * <p>
 * Note: Calling {@link AreaCreator#create()} or triggering a
 * {@link org.bukkit.event.world.WorldInitEvent WorldInitEvent}
 * within this event can lead to an infinite loop.
 */
public class AreaCreateEvent extends AreaEvent<@NotNull Area> {
    /**
     * This event is fired when a new Area is created.
     *
     * @param area the area that has been created
     */
    @ApiStatus.Internal
    public AreaCreateEvent(@NotNull Area area) {
        super(area);
    }
}
