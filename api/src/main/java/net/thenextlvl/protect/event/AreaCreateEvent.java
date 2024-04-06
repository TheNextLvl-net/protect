package net.thenextlvl.protect.event;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.area.Area;

/**
 * The AreaCreateEvent class represents an event that is fired when a new Area is created.
 *
 * @param <T> The type of the area of this event
 */
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaCreateEvent<T extends Area> extends AreaEvent<T> {
    public AreaCreateEvent(T area) {
        super(area);
    }
}
