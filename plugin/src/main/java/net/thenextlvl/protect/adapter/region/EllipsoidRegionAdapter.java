package net.thenextlvl.protect.adapter.region;

import com.fastasyncworldedit.core.math.Vector3Impl;
import com.sk89q.worldedit.math.BlockVector3Imp;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EllipsoidRegionAdapter implements TagAdapter<EllipsoidRegion> {
    @Override
    public EllipsoidRegion deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var center = context.deserialize(compound.get("center"), BlockVector3Imp.class);
        var radius = context.deserialize(compound.get("radius"), Vector3Impl.class);
        return new EllipsoidRegion(center, radius);
    }

    @Override
    public Tag serialize(EllipsoidRegion region, TagSerializationContext context) {
        var tag = new CompoundTag();
        tag.add("center", context.serialize(region.getCenter().toBlockPoint()));
        tag.add("radius", context.serialize(region.getRadius()));
        return tag;
    }
}
