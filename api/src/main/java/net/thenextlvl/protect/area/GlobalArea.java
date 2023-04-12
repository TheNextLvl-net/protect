package net.thenextlvl.protect.area;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import core.annotation.FieldsAreNonnullByDefault;
import core.annotation.MethodsReturnNonnullByDefault;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class GlobalArea extends Area {
    static final HashMap<World, GlobalArea> areas = new HashMap<>();

    GlobalArea(World world) {
        super(world.getName(), new CuboidRegion(new BukkitWorld(world), BlockVector3.ZERO, BlockVector3.ZERO));
        setPriority(-1);
    }

    @Override
    public String getDisplayName() {
        return "__" + super.getDisplayName() + "__";
    }

    @Override
    @Deprecated
    public boolean delete() {
        return false;
    }

    @Override
    @Deprecated
    public boolean redefine(Region region) {
        return false;
    }

    @Override
    public boolean isGlobalArea() {
        return true;
    }

    @Override
    public boolean isTooBig() {
        return true;
    }

    @Override
    public boolean contains(Location location) {
        return true;
    }

    @Override
    @Deprecated
    public Schematic getSchematic() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not allowed for global areas");
    }
}
