package net.thenextlvl.protect.area;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
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
    public int compareTo(@NotNull Area area) {
        return Integer.compare(getPriority(), area.getPriority());
    }
}
