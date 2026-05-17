package net.thenextlvl.protect.adapters.vector;

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
    public Vector3 deserialize(final Tag tag, final TagDeserializationContext context) {
        final var compound = tag.getAsCompound();
        final var x = compound.get("x").getAsDouble();
        final var y = compound.get("y").getAsDouble();
        final var z = compound.get("z").getAsDouble();
        return Vector3.at(x, y, z);
    }

    @Override
    public Tag serialize(final Vector3 vector, final TagSerializationContext context) {
        return CompoundTag.builder()
                .put("x", vector.x())
                .put("y", vector.y())
                .put("z", vector.z())
                .build();
    }
}
