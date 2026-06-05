package net.thenextlvl.protect.flag;

import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;

/**
 * Optional custom serialization hook for value flags.
 *
 * @param <T> the value type
 */
public interface SerializableFlag<T> {
    T deserialize(Tag tag, TagDeserializationContext context);

    Tag serialize(T value, TagSerializationContext context);
}
