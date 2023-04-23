package net.thenextlvl.protect.event;

import core.annotation.FieldsAreNonnullByDefault;
import core.annotation.MethodsReturnNonnullByDefault;
import core.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;

@Getter
@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class PlayerAreaEvent extends AreaEvent {
    private final Player player;

    protected PlayerAreaEvent(Player player, Area area) {
        super(area);
        this.player = player;
    }
}
