package net.thenextlvl.protect.adapter.object;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.DoubleTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DoubleAdapter implements TagAdapter<Double> {
    @Override
    public Double deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return tag.getAsDouble();
    }

    @Override
    public Tag serialize(Double object, TagSerializationContext context) throws ParserException {
        return new DoubleTag(object);
    }
}
