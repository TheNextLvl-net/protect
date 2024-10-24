package net.thenextlvl.protect.area.event.region;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the region associated with an area is about to be redefined.
 * Cancelling this event results in the region not being redefined.
 *
 * @param <T> The type of region associated with the area of this event
 */
@Getter
@Setter
public class AreaRedefineEvent<T extends Region> extends AreaEvent<@NotNull RegionizedArea<@NotNull T>> implements Cancellable {
    private boolean cancelled;
    private @NotNull T region;

    /**
     * Constructs a new AreaRedefineEvent with the specified parameters.
     *
     * @param area The regionized area associated with this event
     * @param region The region that is to be redefined
     */
    @ApiStatus.Internal
    public AreaRedefineEvent(RegionizedArea<@NotNull T> area, @NotNull T region) {
        super(area);
        this.region = region;
    }
}
