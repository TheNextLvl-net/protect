package net.thenextlvl.protect.adapter.object;

import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class UUIDAdapter implements TagAdapter<UUID> {
    @Override
    public UUID deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var most = compound.get("most").getAsLong();
        var least = compound.get("least").getAsLong();
        return new UUID(most, least);
    }

    @Override
    public Tag serialize(UUID uuid, TagSerializationContext context) {
        var tag = new CompoundTag();
        tag.add("most", uuid.getMostSignificantBits());
        tag.add("least", uuid.getLeastSignificantBits());
        return tag;
    }
}
