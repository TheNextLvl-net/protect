package net.thenextlvl.protect.adapter.other;

import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.ListTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public class MembersAdapter implements TagAdapter<Set<UUID>> {
    @Override
    public Set<UUID> deserialize(Tag tag, TagDeserializationContext context) {
        return tag.<CompoundTag>getAsList().stream()
                .map(compound -> context.deserialize(compound, UUID.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Tag serialize(Set<UUID> object, TagSerializationContext context) {
        return new ListTag<>(object.stream()
                .map(context::serialize)
                .toList(), CompoundTag.ID);
    }
}
