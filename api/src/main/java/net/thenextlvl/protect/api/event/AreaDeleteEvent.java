package net.thenextlvl.protect.api.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.api.area.Area;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class AreaDeleteEvent extends AreaEvent implements Cancellable {
    private boolean cancelled;

    public AreaDeleteEvent(Area area) {
        super(area);
    }
}
