package net.thenextlvl.protect.adapter.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CuboidRegionAdapter implements TagAdapter<CuboidRegion> {
    @Override
    public CuboidRegion deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var pos1 = context.deserialize(compound.get("pos1"), BlockVector3.class);
        var pos2 = context.deserialize(compound.get("pos2"), BlockVector3.class);
        return new CuboidRegion(pos1, pos2);
    }

    @Override
    public Tag serialize(CuboidRegion region, TagSerializationContext context) {
        var tag = new CompoundTag();
        tag.add("pos1", context.serialize(region.getPos1()));
        tag.add("pos2", context.serialize(region.getPos2()));
        return tag;
    }
}
