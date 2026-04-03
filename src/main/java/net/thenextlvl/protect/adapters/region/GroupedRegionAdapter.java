package net.thenextlvl.protect.adapters.region;

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
public final class GroupedRegionAdapter implements TagAdapter<GroupedRegion> {
    @Override
    public GroupedRegion deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var compound = tag.getAsCompound();
        final var type = type(compound.get("type").getAsString());
        final var regions = compound.getAsCompound("regions");

        final var map = new HashMap<String, Region>();
        regions.forEach((name, value) -> map.put(name, context.deserialize(value, type)));

        return new GroupedRegion(map);
    }

    @Override
    public Tag serialize(final GroupedRegion group, final TagSerializationContext context) throws ParserException {
        final var tag = CompoundTag.builder();
        tag.put("type", type(group.getRegions().values().iterator().next().getClass()));
        final var regions = CompoundTag.builder();
        group.getRegions().forEach((name, region) -> regions.put(name, context.serialize(region)));
        tag.put("regions", regions.build());
        return tag.build();
    }

    public String type(final Class<? extends Region> type) {
        if (type == CuboidRegion.class) return "cuboid";
        if (type == CylinderRegion.class) return "cylinder";
        if (type == EllipsoidRegion.class) return "ellipsoid";
        throw new UnsupportedRegionException(type);
    }

    public Class<? extends Region> type(final String type) {
        return switch (type) {
            case "cuboid" -> CuboidRegion.class;
            case "ellipsoid" -> EllipsoidRegion.class;
            case "cylinder" -> CylinderRegion.class;
            default -> throw new UnsupportedRegionException(Region.class, type);
        };
    }
}
