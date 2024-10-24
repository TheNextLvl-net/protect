package net.thenextlvl.protect.area.event.player;

import lombok.Getter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;


/**
 * This class represents an abstract base class for events related to areas and players.
 */
@Getter
public abstract class PlayerAreaEvent extends PlayerEvent {
    private static final @Getter HandlerList handlerList = new HandlerList();
    private final @NotNull Area area;

    /**
     * Constructs a new PlayerAreaEvent with the specified player and area.
     *
     * @param player the player associated with this event
     * @param area   the area associated with this event
     */
    protected PlayerAreaEvent(@NotNull Player player, @NotNull Area area) {
        super(player);
        this.area = area;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }
}
