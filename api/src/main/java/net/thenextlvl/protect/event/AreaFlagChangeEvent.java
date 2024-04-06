package net.thenextlvl.protect.event;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class AreaFlagChangeEvent<T> extends AreaEvent<@NotNull Area> implements Cancellable {
    private final @NotNull Flag<T> flag;
    private boolean cancelled;
    private T newState;

    public AreaFlagChangeEvent(@NotNull Area area, @NotNull Flag<T> flag, T newState) {
        super(area);
        this.flag = flag;
        this.newState = newState;
    }
}
