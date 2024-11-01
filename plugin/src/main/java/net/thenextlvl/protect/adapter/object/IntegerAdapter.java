package net.thenextlvl.protect.adapter.object;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.IntTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IntegerAdapter implements TagAdapter<Integer> {
    @Override
    public Integer deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return tag.getAsInt();
    }

    @Override
    public Tag serialize(Integer object, TagSerializationContext context) throws ParserException {
        return new IntTag(object);
    }
}
