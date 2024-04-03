package net.thenextlvl.protect.area;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class CraftGlobalArea extends CraftArea implements GlobalArea {
    private static final Map<World, CraftGlobalArea> globalAreas = new HashMap<>();

    @SuppressWarnings("PatternValidation")
    private CraftGlobalArea(World world, int priority) {
        super("@" + world.getName(), world, priority);
    }

    private CraftGlobalArea(World world) {
        this(world, -1);
    }

    public static CraftGlobalArea of(World world, int priority) {
        return globalAreas.computeIfAbsent(world, w -> new CraftGlobalArea(w, priority));
    }

    public static CraftGlobalArea of(World world) {
        return globalAreas.computeIfAbsent(world, CraftGlobalArea::new);
    }
}
