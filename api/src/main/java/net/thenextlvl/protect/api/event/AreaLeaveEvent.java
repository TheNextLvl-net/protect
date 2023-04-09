package net.thenextlvl.protect.api.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.api.area.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class AreaLeaveEvent extends PlayerAreaEvent implements Cancellable {
    private boolean cancelled;

    public AreaLeaveEvent(Player player, Area area) {
        super(player, area);
    }
}
