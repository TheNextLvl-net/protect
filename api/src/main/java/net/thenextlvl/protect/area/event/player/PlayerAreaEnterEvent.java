package net.thenextlvl.protect.area.event.player;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is triggered when a player enters an area.
 * <p>
 * Players entering an area can be intercepted by listening for this event.
 * If the event is cancelled, the player is prevented from entering the area.
 */
@Getter
@Setter
public class PlayerAreaEnterEvent extends PlayerAreaEvent implements Cancellable {
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaEnterEvent with the specified player and area.
     *
     * @param player the player associated with this event
     * @param area   the area associated with this event
     */
    @ApiStatus.Internal
    public PlayerAreaEnterEvent(@NotNull Player player, @NotNull Area area) {
        super(player, area);
    }
}
