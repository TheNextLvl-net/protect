package net.thenextlvl.protect.area.event.member;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * An event that represents a change in the owner of an area.
 * Cancelling this event results in the owner not being changed.
 *
 * @param <T> The type of the area associated with this event
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaOwnerChangeEvent<T extends RegionizedArea<?>> extends AreaEvent<T> implements Cancellable {
    private @Nullable UUID owner;
    private boolean cancelled;

    public AreaOwnerChangeEvent(T area, @Nullable UUID owner) {
        super(area);
        this.owner = owner;
    }
}
