package net.thenextlvl.protect.area.event.flag;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event triggered by the /area protect command.
 * This event occurs when an area protection command is issued, signaling
 * that a specific area is to be protected. Users can listen to this event
 * to perform actions related to area protection.
 */
@Getter
@Setter
public class AreaProtectEvent extends AreaEvent<@NotNull Area> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs an AreaProtectEvent.
     *
     * @param area The Area to be protected
     */
    @ApiStatus.Internal
    public AreaProtectEvent(@NotNull Area area) {
        super(area);
    }
}
