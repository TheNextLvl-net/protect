package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.area.GlobalArea;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Map;

@RequiredArgsConstructor
public class GlobalAreaAdapter implements JsonSerializer<GlobalArea>, JsonDeserializer<GlobalArea> {
    private final World world;

    @Override
    public JsonElement serialize(GlobalArea area, Type type, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("type", "global");
        object.addProperty("priority", area.getPriority());
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }

    @Override
    public GlobalArea deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        var object = element.getAsJsonObject();
        var area = CraftGlobalArea.of(world, object.get("priority").getAsInt());
        area.setFlags(context.deserialize(object.get("flags"), Map.class));
        return area;
    }
}
