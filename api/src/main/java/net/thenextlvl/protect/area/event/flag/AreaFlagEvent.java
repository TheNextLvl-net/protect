package net.thenextlvl.protect.area.event.flag;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.event.AreaEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an abstract event that is bound to an area and a flag
 *
 * @param <T> The type of the flag value.
 */
@Getter
@Setter
public abstract class AreaFlagEvent<T> extends AreaEvent<@NotNull Area> implements Cancellable {
    private final @NotNull Flag<T> flag;
    private boolean cancelled;

    protected AreaFlagEvent(@NotNull Area area, @NotNull Flag<T> flag) {
        super(area);
        this.flag = flag;
    }
}
