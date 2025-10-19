package net.thenextlvl.protect.adapter.vector;

import com.sk89q.worldedit.math.Vector2;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Vector2Adapter implements TagAdapter<Vector2> {
    @Override
    public Vector2 deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var x = compound.get("x").getAsInt();
        var z = compound.get("z").getAsInt();
        return Vector2.at(x, z);
    }

    @Override
    public Tag serialize(Vector2 vector, TagSerializationContext context) {
        var tag = CompoundTag.empty();
        tag.add("x", vector.x());
        tag.add("z", vector.z());
        return tag;
    }
}
