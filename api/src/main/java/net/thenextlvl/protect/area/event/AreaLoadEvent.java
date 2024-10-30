package net.thenextlvl.protect.area.event;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaCreator;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when an Area is loaded.
 * <p>
 * Note: Calling {@link AreaCreator#create()} within this event can lead to an infinite loop.
 */
@NullMarked
public class AreaLoadEvent extends AreaEvent<Area> {
    /**
     * Constructs a new AreaLoadEvent.
     *
     * @param area the area that has been loaded
     */
    public AreaLoadEvent(Area area) {
        super(area);
    }
}
