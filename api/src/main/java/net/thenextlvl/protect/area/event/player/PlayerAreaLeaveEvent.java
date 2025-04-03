package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;


/**
 * This event is triggered when a player leaves an area.
 * <p>
 * Players leaving an area can be intercepted by listening for this event.
 * If the event is cancelled, the player is prevented from leaving the area.
 */
@NullMarked
public class PlayerAreaLeaveEvent extends PlayerAreaEvent implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaLeaveEvent with the specified player and area.
     *
     * @param player the player who is leaving the area
     * @param area   the area that the player is leaving
     */
    public PlayerAreaLeaveEvent(Player player, Area area) {
        super(player, area);
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
