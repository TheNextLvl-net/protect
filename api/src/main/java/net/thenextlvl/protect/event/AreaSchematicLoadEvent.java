package net.thenextlvl.protect.event;

import com.sk89q.worldedit.regions.Region;
import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.area.RegionizedArea;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The AreaSchematicLoadEvent class represents an event that is triggered when a schematic is loaded for a regionized area.
 * It extends the AreaEvent class and is generic over a type T that must extend the Region class.
 *
 * @param <T> The type of region associated with the area.
 */
@Getter
@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class AreaSchematicLoadEvent<T extends Region> extends AreaEvent<RegionizedArea<T>> {
    private final List<Consumer<RegionizedArea<T>>> successListeners = new ArrayList<>();

    public AreaSchematicLoadEvent(RegionizedArea<T> area) {
        super(area);
    }
}
