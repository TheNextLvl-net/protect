package net.thenextlvl.protect.event;

import lombok.Getter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Getter
public abstract class PlayerAreaEvent extends PlayerEvent {
    private static final @Getter HandlerList handlerList = new HandlerList();
    private final Area area;

    public PlayerAreaEvent(Player player, Area area) {
        super(player);
        this.area = area;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
