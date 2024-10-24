package net.thenextlvl.protect.area.event;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.area.Area;

/**
 * The AreaLoadEvent class represents an event that is fired when an Area is loaded.
 */
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaLoadEvent extends AreaEvent<Area> {
    public AreaLoadEvent(Area area) {
        super(area);
    }
}
