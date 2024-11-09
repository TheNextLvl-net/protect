package net.thenextlvl.protect.adapter.region;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.UnsupportedRegionException;
import net.thenextlvl.protect.region.GroupedRegion;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;

@NullMarked
@RequiredArgsConstructor
public class GroupedRegionAdapter implements TagAdapter<GroupedRegion> {
    private final ProtectPlugin plugin;

    @Override
    public GroupedRegion deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var compound = tag.getAsCompound();
        var type = type(compound.get("type").getAsString());
        var regions = compound.getAsCompound("regions");

        var map = new HashMap<String, Region>();
        regions.forEach((name, value) -> map.put(name, context.deserialize(value, type)));

        return new GroupedRegion(map);
    }

    @Override
    public Tag serialize(GroupedRegion group, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("type", type(group.getRegions().values().iterator().next().getClass()));
        var regions = new CompoundTag();
        group.getRegions().forEach((name, region) -> regions.add(name, context.serialize(region)));
        tag.add("regions", regions);
        return tag;
    }

    public String type(Class<? extends Region> type) {
        if (type == CuboidRegion.class) return "cuboid";
        if (type == CylinderRegion.class) return "cylinder";
        if (type == EllipsoidRegion.class) return "ellipsoid";
        throw new UnsupportedRegionException(type);
    }

    public Class<? extends Region> type(String type) {
        return switch (type) {
            case "cuboid" -> CuboidRegion.class;
            case "ellipsoid" -> EllipsoidRegion.class;
            case "cylinder" -> CylinderRegion.class;
            default -> throw new UnsupportedRegionException(Region.class, type);
        };
    }
}
