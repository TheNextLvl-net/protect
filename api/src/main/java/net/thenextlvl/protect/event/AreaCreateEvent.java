package net.thenextlvl.protect.event;

import net.thenextlvl.protect.area.Area;

public class AreaCreateEvent extends AreaEvent {
    public AreaCreateEvent(Area area) {
        super(area);
    }
}
