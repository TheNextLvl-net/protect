package net.thenextlvl.protect.adapter.area;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;


public class GlobalAreaAdapter implements AreaAdapter<CraftGlobalArea> {
    private final @Getter NamespacedKey namespacedKey;
    private final ProtectPlugin plugin;

    public GlobalAreaAdapter(ProtectPlugin plugin) {
        this.namespacedKey = new NamespacedKey(plugin, "global");
        this.plugin = plugin;
    }

    @Override
    public CraftGlobalArea deserialize(JsonObject object, World world, JsonDeserializationContext context) {
        var priority = object.get("priority").getAsInt();
        var flags = context.<Map<Flag<?>, @Nullable Object>>deserialize(object.get("flags"), LinkedHashMap.class);
        return new CraftGlobalArea(plugin, world, flags, priority);
    }

    @Override
    public JsonObject serialize(CraftGlobalArea area, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("priority", area.getPriority());
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }
}
