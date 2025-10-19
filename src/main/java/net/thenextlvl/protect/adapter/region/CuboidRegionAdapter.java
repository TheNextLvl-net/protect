package net.thenextlvl.protect.adapter.region;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CuboidRegionAdapter implements TagAdapter<CuboidRegion> {
    @Override
    public CuboidRegion deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var pos1 = context.deserialize(compound.get("pos1"), BlockVector3.class);
        var pos2 = context.deserialize(compound.get("pos2"), BlockVector3.class);
        return new CuboidRegion(pos1, pos2);
    }

    @Override
    public Tag serialize(CuboidRegion region, TagSerializationContext context) {
        var tag = CompoundTag.empty();
        tag.add("pos1", context.serialize(region.getPos1()));
        tag.add("pos2", context.serialize(region.getPos2()));
        return tag;
    }
}
