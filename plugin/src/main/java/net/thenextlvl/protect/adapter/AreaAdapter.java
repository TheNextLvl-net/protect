package net.thenextlvl.protect.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CuboidArea;
import net.thenextlvl.protect.area.GlobalArea;

import java.lang.reflect.Type;

public class AreaAdapter implements JsonDeserializer<Area> {

    @Override
    public Area deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        var areaType = element.getAsJsonObject().get("type").getAsString();
        return switch (areaType) {
            case "cuboid" -> context.deserialize(element, CuboidArea.class);
            case "global" -> context.deserialize(element, GlobalArea.class);
            default -> throw new IllegalStateException("Unexpected value: " + areaType);
        };
    }
}
