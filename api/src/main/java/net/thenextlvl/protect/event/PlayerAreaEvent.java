package net.thenextlvl.protect.event;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


/**
 * This class represents an abstract base class for events related to areas and players.
 *
 * @param <T> The type of the area associated with the event.
 */
@Getter
@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public abstract class PlayerAreaEvent<T extends Area> extends PlayerEvent {
    private static final @Getter HandlerList handlerList = new HandlerList();
    private final T area;

    public PlayerAreaEvent(Player player, T area) {
        super(player);
        this.area = area;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
