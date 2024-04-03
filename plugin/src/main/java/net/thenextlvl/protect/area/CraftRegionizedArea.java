package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.event.AreaRedefineEvent;
import org.bukkit.World;

import java.io.File;

@Getter
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public abstract class CraftRegionizedArea<T extends Region> extends CraftArea implements RegionizedArea<T> {
    private final File schematic;
    private T region;

    protected CraftRegionizedArea(ProtectPlugin plugin, @NamePattern String name, World world, T region, int priority) throws IllegalArgumentException {
        super(name, world, priority);
        this.schematic = new File(plugin.schematicFolder(), name + ".schem");
        this.region = region;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean setRegion(T region) {
        var clone = (T) region.clone();
        if (!new AreaRedefineEvent<>(this, clone).callEvent()) return false;
        this.region = clone;
        return true;
    }
}
