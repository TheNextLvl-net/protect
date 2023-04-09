package net.thenextlvl.protect.api.event;

import net.thenextlvl.protect.api.area.Area;

public class AreaCreateEvent extends AreaEvent {
    public AreaCreateEvent(Area area) {
        super(area);
    }
}
