package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when an Area is loaded.
 * <p>
 * Note: Calling {@link AreaCreator#create()} within this event can lead to an infinite loop.
 */
public class AreaLoadEvent extends AreaEvent<@NotNull Area> {
    /**
     * Constructs a new AreaLoadEvent.
     *
     * @param area the area that has been loaded
     */
    public AreaLoadEvent(@NotNull Area area) {
        super(area);
    }
}
