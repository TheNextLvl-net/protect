package net.thenextlvl.protect.area;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thenextlvl.protect.event.AreaFlagChangeEvent;
import net.thenextlvl.protect.event.AreaFlagUnsetEvent;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class CraftArea implements Area {
    private Map<Flag<?>, @Nullable Object> flags = new LinkedHashMap<>();
    private final @NamePattern String name;
    private final World world;
    private int priority;

    protected CraftArea(@NamePattern String name, World world, int priority) {
        this.name = name;
        this.world = world;
        this.priority = priority;
    }

    @Override
    public <T> void setFlag(@NotNull Flag<T> flag, T state) {
        var event = new AreaFlagChangeEvent<>(this, flag, state);
        if (event.callEvent()) Area.super.setFlag(flag, event.getNewState());
    }

    @Override
    public <T> boolean removeFlag(@NotNull Flag<T> flag) {
        var event = new AreaFlagUnsetEvent<>(this, flag);
        return event.callEvent() && Area.super.removeFlag(flag);
    }

    @Override
    public int compareTo(@NotNull Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }
}
