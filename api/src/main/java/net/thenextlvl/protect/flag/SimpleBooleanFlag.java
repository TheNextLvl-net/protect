package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.ByteTag;
import net.thenextlvl.nbt.tag.Tag;

final class SimpleBooleanFlag extends SimpleFlag implements SerializableFlag<Boolean>, BooleanFlag {
    private final boolean defaultValue;
    private final boolean value;

    SimpleBooleanFlag(final Key key, final boolean defaultValue) {
        this(key, defaultValue, defaultValue);
    }

    private SimpleBooleanFlag(final Key key, final boolean defaultValue, final boolean value) {
        super(key);
        this.defaultValue = defaultValue;
        this.value = value;
    }

    @Override
    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public BooleanFlag withValue(final boolean value) {
        return new SimpleBooleanFlag(key(), defaultValue, value);
    }

    @Override
    public Boolean deserialize(final Tag tag, final TagDeserializationContext context) {
        return tag.getAsBoolean();
    }

    @Override
    public Tag serialize(final Boolean value, final TagSerializationContext context) {
        return ByteTag.of(value);
    }
}   
