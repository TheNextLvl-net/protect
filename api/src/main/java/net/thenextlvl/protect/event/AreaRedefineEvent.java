package net.thenextlvl.protect.event;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class AreaRedefineEvent extends AreaEvent implements Cancellable {
    private final Region region;
    private boolean cancelled;

    public AreaRedefineEvent(Area area, Region region) {
        super(area);
        this.region = region;
    }
}
