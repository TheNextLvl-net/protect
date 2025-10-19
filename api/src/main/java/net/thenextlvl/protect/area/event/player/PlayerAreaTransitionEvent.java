package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered whenever a player transitions to a new area.
 * Cancelling this event results in the player not being able to move to the new area.
 */
@NullMarked
public final class PlayerAreaTransitionEvent extends PlayerAreaEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final Area previous;
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaTransitionEvent.
     *
     * @param player the player involved in the area transition
     * @param from   the area the player is transitioning from
     * @param to     the area the player is transitioning to
     */
    @ApiStatus.Internal
    public PlayerAreaTransitionEvent(Player player, Area from, Area to) {
        super(player, to);
        this.previous = from;
    }

    @Contract(pure = true)
    public Area getPrevious() {
        return previous;
    }

    @Override
    @Contract(pure = true)
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    @Contract(mutates = "this")
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    @Contract(pure = true)
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
