package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.LongTag;
import net.thenextlvl.nbt.tag.Tag;

final class SimpleLongFlag extends SimpleFlag implements SerializableFlag<Long>, LongFlag {
    private final long defaultValue;
    private final long value;

    SimpleLongFlag(final Key key, final long defaultValue) {
        this(key, defaultValue, defaultValue);
    }

    private SimpleLongFlag(final Key key, final long defaultValue, final long value) {
        super(key);
        this.defaultValue = defaultValue;
        this.value = value;
    }

    @Override
    public long getDefaultValue() {
        return defaultValue;
    }

    @Override
    public long getValue() {
        return value;
    }

    @Override
    public LongFlag withValue(final long value) {
        return new SimpleLongFlag(key(), defaultValue, value);
    }

    @Override
    public Long deserialize(final Tag tag, final TagDeserializationContext context) {
        return tag.getAsLong();
    }

    @Override
    public Tag serialize(final Long value, final TagSerializationContext context) {
        return LongTag.of(value);
    }
}   
