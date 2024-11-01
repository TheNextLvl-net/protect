package net.thenextlvl.protect.adapter.object;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.ShortTag;
import core.nbt.tag.Tag;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ShortAdapter implements TagAdapter<Short> {
    @Override
    public Short deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return tag.getAsShort();
    }

    @Override
    public Tag serialize(Short object, TagSerializationContext context) throws ParserException {
        return new ShortTag(object);
    }
}
