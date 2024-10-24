package net.thenextlvl.protect.area.event.schematic;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a schematic of a regionized area is loaded.
 * This event is cancellable, meaning it can be prevented from proceeding.
 */
@Getter
@Setter
public class AreaSchematicLoadEvent extends AreaEvent<@NotNull RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new AreaSchematicLoadEvent.
     *
     * @param area The regionized area whose schematic is being loaded.
     */
    @ApiStatus.Internal
    public AreaSchematicLoadEvent(@NotNull RegionizedArea<?> area) {
        super(area);
    }
}
