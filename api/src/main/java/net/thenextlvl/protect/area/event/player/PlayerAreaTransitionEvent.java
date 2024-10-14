package net.thenextlvl.protect.area.event.player;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

/**
 * This event is triggered whenever a player transitions to a new area.
 * Cancelling this event result in the player not being able to move to the new area.
 */
@Getter
@Setter
@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class PlayerAreaTransitionEvent extends PlayerAreaEvent<Area> implements Cancellable {
    private final Area previous;
    private boolean cancelled;

    public PlayerAreaTransitionEvent(Player player, Area from, Area to) {
        super(player, to);
        this.previous = from;
    }
}
