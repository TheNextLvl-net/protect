package net.thenextlvl.protect.area.event.player;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;


/**
 * This event is triggered when a player leaves an area.
 * <p>
 * Players leaving an area can be intercepted by listening for this event.
 * If the event is cancelled, the player is prevented from leaving the area.
 */
@Getter
@Setter
public class PlayerAreaLeaveEvent extends PlayerAreaEvent implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaLeaveEvent with the specified player and area.
     *
     * @param player the player who is leaving the area
     * @param area   the area that the player is leaving
     */
    @ApiStatus.Internal
    public PlayerAreaLeaveEvent(@NotNull Player player, @NotNull Area area) {
        super(player, area);
    }
}
