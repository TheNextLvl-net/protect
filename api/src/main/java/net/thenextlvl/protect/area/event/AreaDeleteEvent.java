package net.thenextlvl.protect.area.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.AreaService;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is triggered when an Area is requested for deletion.
 * Cancelling this event results in the area not being deleted.
 * <p>
 * Note: Calling {@link AreaService#delete(RegionizedArea)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
public class AreaDeleteEvent extends AreaEvent<@NotNull RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs an AreaDeleteEvent.
     *
     * @param area the RegionizedArea that is requested to be deleted
     */
    @ApiStatus.Internal
    public AreaDeleteEvent(@NotNull RegionizedArea<?> area) {
        super(area);
    }
}
