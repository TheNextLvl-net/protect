package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * This class represents an abstract base class for events related to areas and players.
 */
@NullMarked
public abstract class PlayerAreaEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Area area;

    /**
     * Constructs a new PlayerAreaEvent with the specified player and area.
     *
     * @param player the player associated with this event
     * @param area   the area associated with this event
     */
    protected PlayerAreaEvent(Player player, Area area) {
        super(player);
        this.area = area;
    }

    public Area getArea() {
        return area;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
