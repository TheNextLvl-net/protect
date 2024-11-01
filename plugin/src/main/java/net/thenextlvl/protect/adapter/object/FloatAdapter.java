package net.thenextlvl.protect.adapter.object;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.FloatTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FloatAdapter implements TagAdapter<Float> {
    @Override
    public Float deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return tag.getAsFloat();
    }

    @Override
    public Tag serialize(Float object, TagSerializationContext context) throws ParserException {
        return new FloatTag(object);
    }
}
