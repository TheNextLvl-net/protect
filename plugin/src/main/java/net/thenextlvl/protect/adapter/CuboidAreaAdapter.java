package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftCuboidArea;
import net.thenextlvl.protect.area.CuboidArea;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.Map;

@RequiredArgsConstructor
public class CuboidAreaAdapter implements JsonSerializer<CuboidArea>, JsonDeserializer<CuboidArea> {
    private final ProtectPlugin plugin;
    private final World world;

    @Override
    public JsonElement serialize(CuboidArea area, Type type, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("type", "cuboid");
        object.addProperty("name", area.getName());
        object.addProperty("priority", area.getPriority());
        object.add("region", context.serialize(area.getRegion()));
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public CuboidArea deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        var object = element.getAsJsonObject();
        var name = object.get("name").getAsString();
        var priority = object.get("priority").getAsInt();
        var region = context.<CuboidRegion>deserialize(object.get("region"), CuboidRegion.class);
        var area = new CraftCuboidArea(plugin, name, world, region, priority);
        area.setFlags(context.deserialize(object.get("flags"), Map.class));
        return area;
    }
}
