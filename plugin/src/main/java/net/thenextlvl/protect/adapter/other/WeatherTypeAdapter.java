package net.thenextlvl.protect.adapter.other;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;
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
        return StringTag.of(type.name());
    }
}
