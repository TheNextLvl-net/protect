package net.thenextlvl.protect.area.event;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.area.Area;

/**
 * The AreaCreateEvent class represents an event that is fired when a new Area is created.
 */
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaCreateEvent extends AreaEvent<Area> {
    public AreaCreateEvent(Area area) {
        super(area);
    }
}
