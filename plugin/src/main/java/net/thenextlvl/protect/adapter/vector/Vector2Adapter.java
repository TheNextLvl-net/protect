package net.thenextlvl.protect.adapter.vector;

import com.sk89q.worldedit.math.Vector2;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Vector2Adapter implements TagAdapter<Vector2> {
    @Override
    public Vector2 deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var x = compound.get("x").getAsInt();
        var z = compound.get("z").getAsInt();
        return Vector2.at(x, z);
    }

    @Override
    public Tag serialize(Vector2 vector, TagSerializationContext context) {
        var tag = new CompoundTag();
        tag.add("x", vector.x());
        tag.add("z", vector.z());
        return tag;
    }
}
