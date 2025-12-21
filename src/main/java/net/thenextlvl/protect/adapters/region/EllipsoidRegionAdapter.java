package net.thenextlvl.protect.adapters.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EllipsoidRegionAdapter implements TagAdapter<EllipsoidRegion> {
    @Override
    public EllipsoidRegion deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var center = context.deserialize(compound.get("center"), BlockVector3.class);
        var radius = context.deserialize(compound.get("radius"), Vector3.class);
        return new EllipsoidRegion(center, radius);
    }

    @Override
    public Tag serialize(EllipsoidRegion region, TagSerializationContext context) {
        return CompoundTag.builder()
                .put("center", context.serialize(region.getCenter().toBlockPoint()))
                .put("radius", context.serialize(region.getRadius()))
                .build();
    }
}
