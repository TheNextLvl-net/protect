package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered whenever a player transitions to a new area.
 * Cancelling this event results in the player not being able to move to the new area.
 */
@NullMarked
public class PlayerAreaTransitionEvent extends PlayerAreaEvent implements Cancellable {
    private final Area previous;
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaTransitionEvent.
     *
     * @param player the player involved in the area transition
     * @param from   the area the player is transitioning from
     * @param to     the area the player is transitioning to
     */
    public PlayerAreaTransitionEvent(Player player, Area from, Area to) {
        super(player, to);
        this.previous = from;
    }

    public Area getPrevious() {
        return previous;
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
