package net.thenextlvl.protect.adapter.object;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StringAdapter implements TagAdapter<String> {
    @Override
    public String deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return tag.getAsString();
    }

    @Override
    public Tag serialize(String object, TagSerializationContext context) throws ParserException {
        return new StringTag(object);
    }
}
