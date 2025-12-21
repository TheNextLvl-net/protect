package net.thenextlvl.protect.adapters.other;

import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
public final class MembersAdapter implements TagAdapter<Set<UUID>> {
    @Override
    public Set<UUID> deserialize(Tag tag, TagDeserializationContext context) {
        return tag.<CompoundTag>getAsList().stream()
                .map(compound -> context.deserialize(compound, UUID.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Tag serialize(Set<UUID> object, TagSerializationContext context) {
        return ListTag.of(CompoundTag.ID, object.stream()
                .map(context::serialize)
                .toList());
    }
}
