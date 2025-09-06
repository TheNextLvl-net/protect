package net.thenextlvl.protect.adapter.region;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Region;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import net.thenextlvl.protect.exception.UnsupportedRegionException;
import net.thenextlvl.protect.region.GroupedRegion;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;

@NullMarked
public class GroupedRegionAdapter implements TagAdapter<GroupedRegion> {
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
        var tag = CompoundTag.empty();
        tag.add("type", type(group.getRegions().values().iterator().next().getClass()));
        var regions = CompoundTag.empty();
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
