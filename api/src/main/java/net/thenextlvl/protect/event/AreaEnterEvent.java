package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class AreaEnterEvent extends PlayerAreaEvent implements Cancellable {
    private boolean cancelled;

    public AreaEnterEvent(Player player, Area area) {
        super(player, area);
    }
}
