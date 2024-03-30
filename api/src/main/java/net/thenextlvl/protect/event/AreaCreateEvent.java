package net.thenextlvl.protect.event;

import net.thenextlvl.protect.area.Area;

/**
 * The AreaCreateEvent class represents an event that is fired when a new Area is created.
 *
 * @param <T> The type of the area of this event
 */
public class AreaCreateEvent<T extends Area> extends AreaEvent<T> {
    public AreaCreateEvent(T area) {
        super(area);
    }
}
