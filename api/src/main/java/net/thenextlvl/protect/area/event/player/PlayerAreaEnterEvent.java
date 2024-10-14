package net.thenextlvl.protect.area.event.player;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


/**
 * This event is triggered when a player enters an area.
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class PlayerAreaEnterEvent extends PlayerAreaEvent<Area> implements Cancellable {
    private boolean cancelled;

    public PlayerAreaEnterEvent(Player player, Area area) {
        super(player, area);
    }
}
