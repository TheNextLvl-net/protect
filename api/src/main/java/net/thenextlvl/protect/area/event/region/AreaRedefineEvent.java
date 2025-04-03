package net.thenextlvl.protect.area.event.region;

import com.sk89q.worldedit.regions.Region;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * This event is fired when the region associated with an area is about to be redefined.
 * Cancelling this event results in the region not being redefined.
 *
 * @param <T> The type of region associated with the area of this event
 */
@NullMarked
public class AreaRedefineEvent<T extends Region> extends AreaEvent<RegionizedArea<T>> implements Cancellable {
    private boolean cancelled;
    private T region;

    /**
     * Constructs a new AreaRedefineEvent with the specified parameters.
     *
     * @param area   The regionized area associated with this event
     * @param region The region that is to be redefined
     */
    public AreaRedefineEvent(RegionizedArea<T> area, T region) {
        super(area);
        this.region = region;
    }

    public T getRegion() {
        return region;
    }

    public void setRegion(T region) {
        this.region = region;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
