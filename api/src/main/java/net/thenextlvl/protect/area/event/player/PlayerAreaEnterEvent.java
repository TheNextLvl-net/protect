package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This event is triggered when a player enters an area.
 * <p>
 * Players entering an area can be intercepted by listening for this event.
 * If the event is cancelled, the player is prevented from entering the area.
 */
@NullMarked
public final class PlayerAreaEnterEvent extends PlayerAreaEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaEnterEvent with the specified player and area.
     *
     * @param player the player associated with this event
     * @param area   the area associated with this event
     */
    @ApiStatus.Internal
    public PlayerAreaEnterEvent(Player player, Area area) {
        super(player, area);
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
