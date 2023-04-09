package net.thenextlvl.protect.api.event;

import lombok.Getter;
import net.thenextlvl.protect.api.area.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class AreaSchematicLoadEvent extends AreaEvent {
    private final List<Consumer<Area>> successListeners = new ArrayList<>();

    public AreaSchematicLoadEvent(Area area) {
        super(area);
    }
}
