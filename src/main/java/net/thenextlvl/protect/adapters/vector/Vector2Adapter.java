package net.thenextlvl.protect.adapters.vector;

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
    public Vector2 deserialize(final Tag tag, final TagDeserializationContext context) {
        final var compound = tag.getAsCompound();
        final var x = compound.get("x").getAsInt();
        final var z = compound.get("z").getAsInt();
        return Vector2.at(x, z);
    }

    @Override
    public Tag serialize(final Vector2 vector, final TagSerializationContext context) {
        return CompoundTag.builder()
                .put("x", vector.x())
                .put("z", vector.z())
                .build();
    }
}
