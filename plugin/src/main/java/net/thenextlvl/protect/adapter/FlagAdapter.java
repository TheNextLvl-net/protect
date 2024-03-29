package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import net.thenextlvl.protect.area.Area;

import java.lang.reflect.Type;

public class FlagAdapter implements JsonSerializer<Area>, JsonDeserializer<Area> {
    @Override
    public Area deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        return null;
    }

    @Override
    public JsonElement serialize(Area area, Type type, JsonSerializationContext context) {
        return null;
    }
}
