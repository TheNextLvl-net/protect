package net.thenextlvl.protect.adapter.other;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.StringTag;
import core.nbt.tag.Tag;
import org.bukkit.WeatherType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class WeatherTypeAdapter implements TagAdapter<WeatherType> {
    @Override
    public WeatherType deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        return WeatherType.valueOf(tag.getAsString());
    }

    @Override
    public Tag serialize(WeatherType type, TagSerializationContext context) throws ParserException {
        return new StringTag(type.name());
    }
}
