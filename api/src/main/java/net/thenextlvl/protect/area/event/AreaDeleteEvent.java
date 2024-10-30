package net.thenextlvl.protect.area.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.AreaService;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when an Area is requested for deletion.
 * Cancelling this event results in the area not being deleted.
 * <p>
 * Note: Calling {@link AreaService#delete(RegionizedArea)} within this event can lead to an infinite loop.
 */
@Getter
@Setter
@NullMarked
public class AreaDeleteEvent extends AreaEvent<RegionizedArea<?>> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs an AreaDeleteEvent.
     *
     * @param area the RegionizedArea that is requested to be deleted
     */
    public AreaDeleteEvent(RegionizedArea<?> area) {
        super(area);
    }
}
