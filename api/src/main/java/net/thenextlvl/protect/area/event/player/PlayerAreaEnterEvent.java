package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when a player enters an area.
 * <p>
 * Players entering an area can be intercepted by listening for this event.
 * If the event is cancelled, the player is prevented from entering the area.
 */
@NullMarked
public class PlayerAreaEnterEvent extends PlayerAreaEvent implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaEnterEvent with the specified player and area.
     *
     * @param player the player associated with this event
     * @param area   the area associated with this event
     */
    public PlayerAreaEnterEvent(Player player, Area area) {
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
