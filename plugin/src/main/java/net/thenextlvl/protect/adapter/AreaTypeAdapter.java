package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Objects;

@RequiredArgsConstructor
public class AreaTypeAdapter implements JsonDeserializer<Area>, JsonSerializer<Area> {
    private final ProtectPlugin plugin;
    private final World world;

    @Override
    public Area deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = element.getAsJsonObject();
        var areaType = context.<NamespacedKey>deserialize(object.get("type"), NamespacedKey.class);
        var adapter = Objects.requireNonNull(plugin.areaService().getAdapter(areaType), "Missing adapter " + areaType);
        return adapter.deserialize(object, world, context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonElement serialize(Area area, Type type, JsonSerializationContext context) {
        var adapter = (AreaAdapter<Area>) plugin.areaService().getAdapter(area.getClass());
        var object = adapter.serialize(area, context);
        object.add("type", context.serialize(adapter.getNamespacedKey()));
        return object;
    }
}
