package net.thenextlvl.protect.event;

import net.thenextlvl.protect.area.Area;

/**
 * The AreaCreateEvent class represents an event that is fired when a new Area is created.
 */
public class AreaCreateEvent extends AreaEvent {
    public AreaCreateEvent(Area area) {
        super(area);
    }
}
