package net.thenextlvl.protect.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftCuboidArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.Map;

public class CuboidAreaAdapter implements AreaAdapter<CraftCuboidArea> {
    private final @Getter NamespacedKey namespacedKey;
    private final ProtectPlugin plugin;

    public CuboidAreaAdapter(ProtectPlugin plugin) {
        this.namespacedKey = new NamespacedKey(plugin, "cuboid");
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public CraftCuboidArea deserialize(JsonObject object, World world, JsonDeserializationContext context) {
        var name = object.get("name").getAsString();
        var priority = object.get("priority").getAsInt();
        var region = context.<CuboidRegion>deserialize(object.get("region"), CuboidRegion.class);
        var area = new CraftCuboidArea(plugin.schematicFolder(), name, world, region, priority);
        area.setFlags(context.deserialize(object.get("flags"), Map.class));
        return area;
    }

    @Override
    public JsonObject serialize(CraftCuboidArea area, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("name", area.getName());
        object.addProperty("priority", area.getPriority());
        object.add("region", context.serialize(area.getRegion()));
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }
}
