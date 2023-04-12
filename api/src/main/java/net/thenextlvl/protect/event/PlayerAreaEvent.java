package net.thenextlvl.protect.event;

import core.annotation.FieldsAreNonnullByDefault;
import core.annotation.MethodsReturnNonnullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;

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
