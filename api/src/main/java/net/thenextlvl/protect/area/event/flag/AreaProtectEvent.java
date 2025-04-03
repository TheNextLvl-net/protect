package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an event triggered by the /area protect command.
 * This event occurs when an area protection command is issued, signaling
 * that a specific area is to be protected. Users can listen to this event
 * to perform actions related to area protection.
 */
@NullMarked
public class AreaProtectEvent extends AreaEvent<Area> implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs an AreaProtectEvent.
     *
     * @param area The Area to be protected
     */
    public AreaProtectEvent(Area area) {
        super(area);
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
