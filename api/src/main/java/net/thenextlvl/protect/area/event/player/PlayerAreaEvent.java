package net.thenextlvl.protect.area.event.player;

import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * This class represents an abstract base class for events related to areas and players.
 */
@NullMarked
public abstract class PlayerAreaEvent extends PlayerEvent {
    private final Area area;

    /**
     * Constructs a new PlayerAreaEvent with the specified player and area.
     *
     * @param player the player associated with this event
     * @param area   the area associated with this event
     */
    @ApiStatus.Internal
    protected PlayerAreaEvent(Player player, Area area) {
        super(player);
        this.area = area;
    }

    @Contract(pure = true)
    public Area getArea() {
        return area;
    }
}
