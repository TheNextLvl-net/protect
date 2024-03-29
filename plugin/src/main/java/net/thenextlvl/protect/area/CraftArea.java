package net.thenextlvl.protect.area;

import lombok.Getter;
import lombok.Setter;
import net.thenextlvl.protect.flag.CraftFlagProvider;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public abstract class CraftArea extends CraftFlagProvider implements Area {
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
