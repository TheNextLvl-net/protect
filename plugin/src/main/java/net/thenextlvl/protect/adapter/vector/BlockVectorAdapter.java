package net.thenextlvl.protect.adapter.vector;

import com.sk89q.worldedit.math.BlockVector3;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BlockVectorAdapter implements TagAdapter<BlockVector3> {
    @Override
    public BlockVector3 deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var x = compound.get("x").getAsInt();
        var y = compound.get("y").getAsInt();
        var z = compound.get("z").getAsInt();
        return BlockVector3.at(x, y, z);
    }

    @Override
    public Tag serialize(BlockVector3 vector, TagSerializationContext context) {
        var tag = CompoundTag.empty();
        tag.add("x", vector.x());
        tag.add("y", vector.y());
        tag.add("z", vector.z());
        return tag;
    }
}
