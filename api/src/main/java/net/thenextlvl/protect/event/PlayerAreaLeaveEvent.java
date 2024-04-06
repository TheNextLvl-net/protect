package net.thenextlvl.protect.event;

import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;


/**
 * This event is triggered when a player leaves an area.
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@ParametersAreNotNullByDefault
public class PlayerAreaLeaveEvent extends PlayerAreaEvent<Area> implements Cancellable {
    private boolean cancelled;

    public PlayerAreaLeaveEvent(Player player, Area area) {
        super(player, area);
    }
}
