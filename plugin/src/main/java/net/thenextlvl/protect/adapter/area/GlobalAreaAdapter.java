package net.thenextlvl.protect.adapter.area;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.Map;

@Getter
public class GlobalAreaAdapter implements AreaAdapter<CraftGlobalArea> {
    private final NamespacedKey namespacedKey;

    public GlobalAreaAdapter(ProtectPlugin plugin) {
        this.namespacedKey = new NamespacedKey(plugin, "global");
    }

    @Override
    public CraftGlobalArea deserialize(JsonObject object, World world, JsonDeserializationContext context) {
        var area = new CraftGlobalArea(world, object.get("priority").getAsInt());
        area.setFlags(context.deserialize(object.get("flags"), Map.class));
        return area;
    }

    @Override
    public JsonObject serialize(CraftGlobalArea area, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("priority", area.getPriority());
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }
}
