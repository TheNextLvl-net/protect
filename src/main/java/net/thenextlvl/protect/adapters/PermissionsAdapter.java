package net.thenextlvl.protect.adapters;

import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.ListTag;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public final class PermissionsAdapter implements TagAdapter<Set<String>> {
    @Override
    public Set<String> deserialize(final Tag tag, final TagDeserializationContext context) {
        return tag.<Tag>getAsList().stream()
                .map(Tag::getAsString)
                .collect(Collectors.toSet());
    }

    @Override
    public Tag serialize(final Set<String> object, final TagSerializationContext context) {
        return ListTag.of(StringTag.ID, object.stream()
                .map(StringTag::of)
                .toList());
    }
}