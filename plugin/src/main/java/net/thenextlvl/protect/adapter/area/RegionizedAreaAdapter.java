package net.thenextlvl.protect.adapter.area;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftRegionizedArea;
import net.thenextlvl.protect.area.NamePattern;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.Map;
import java.util.UUID;

public abstract class RegionizedAreaAdapter<C extends Region, T extends CraftRegionizedArea<C>> implements AreaAdapter<T> {
    private final @Getter NamespacedKey namespacedKey;
    protected final ProtectPlugin plugin;

    public RegionizedAreaAdapter(ProtectPlugin plugin, @KeyPattern.Value String name) {
        this.namespacedKey = new NamespacedKey(plugin, name);
        this.plugin = plugin;
    }

    protected abstract T constructArea(@NamePattern.Regionized String name, World world, C region, int priority);

    @Override
    @SuppressWarnings("PatternValidation")
    public T deserialize(JsonObject object, World world, JsonDeserializationContext context) {
        var name = object.get("name").getAsString();
        var priority = object.get("priority").getAsInt();
        var owner = object.has("owner") ? context.<UUID>deserialize(object.get("owner"), UUID.class) : null;
        var region = context.<C>deserialize(object.get("region"), new TypeToken<C>(getClass()) {
        }.getType());
        var area = constructArea(name, world, region, priority);
        area.setFlags(context.deserialize(object.get("flags"), Map.class));
        area.internalSetOwner(owner);
        return area;
    }

    @Override
    public JsonObject serialize(T area, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("name", area.getName());
        object.addProperty("priority", area.getPriority());
        if (area.getOwner() != null) object.add("owner", context.serialize(area.getOwner()));
        object.add("region", context.serialize(area.getRegion()));
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }
}
