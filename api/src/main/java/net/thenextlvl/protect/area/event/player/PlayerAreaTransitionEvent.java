package net.thenextlvl.protect.area.event.player;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * This event is triggered whenever a player transitions to a new area.
 * Cancelling this event results in the player not being able to move to the new area.
 */
@Getter
@Setter
public class PlayerAreaTransitionEvent extends PlayerAreaEvent implements Cancellable {
    private final @NotNull Area previous;
    private boolean cancelled;

    /**
     * Constructs a new PlayerAreaTransitionEvent.
     *
     * @param player the player involved in the area transition
     * @param from   the area the player is transitioning from
     * @param to     the area the player is transitioning to
     */
    @ApiStatus.Internal
    public PlayerAreaTransitionEvent(@NotNull Player player, @NotNull Area from, @NotNull Area to) {
        super(player, to);
        this.previous = from;
    }
}
