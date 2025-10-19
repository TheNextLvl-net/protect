package net.thenextlvl.protect.adapter.vector;

import com.sk89q.worldedit.math.Vector3;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Vector3Adapter implements TagAdapter<Vector3> {
    @Override
    public Vector3 deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var x = compound.get("x").getAsDouble();
        var y = compound.get("y").getAsDouble();
        var z = compound.get("z").getAsDouble();
        return Vector3.at(x, y, z);
    }

    @Override
    public Tag serialize(Vector3 vector, TagSerializationContext context) {
        var tag = CompoundTag.empty();
        tag.add("x", vector.x());
        tag.add("y", vector.y());
        tag.add("z", vector.z());
        return tag;
    }
}
