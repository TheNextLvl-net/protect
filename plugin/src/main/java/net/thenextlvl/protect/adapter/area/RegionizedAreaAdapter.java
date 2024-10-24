package net.thenextlvl.protect.adapter.area;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftAreaCreator;
import net.thenextlvl.protect.area.CraftRegionizedArea;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Accessors(fluent = true)
public abstract class RegionizedAreaAdapter<C extends Region, T extends CraftRegionizedArea<C>> implements AreaAdapter<T> {
    private final @Getter NamespacedKey key;
    protected final ProtectPlugin plugin;

    public RegionizedAreaAdapter(ProtectPlugin plugin, @KeyPattern.Value String name) {
        this.key = new NamespacedKey(plugin, name);
        this.plugin = plugin;
    }

    protected abstract T construct(CraftAreaCreator<C> creator);

    @Override
    public T deserialize(JsonObject object, World world, JsonDeserializationContext context) {
        var name = object.get("name").getAsString();
        var priority = object.get("priority").getAsInt();

        var parent = object.has("parent") ? object.get("parent").getAsString() : null;
        var owner = object.has("owner") ? context.<UUID>deserialize(object.get("owner"), UUID.class) : null;

        var members = Objects.<Set<UUID>>requireNonNullElseGet(context.deserialize(object.get("members"), HashSet.class), HashSet::new);
        var flags = context.<Map<Flag<?>, @Nullable Object>>deserialize(object.get("flags"), LinkedHashMap.class);

        var region = context.<C>deserialize(object.get("region"), new TypeToken<C>(getClass()) {
        }.getType());

        return construct(new CraftAreaCreator<>(plugin, name, world, region, parent, owner, flags, members, priority));
    }

    @Override
    public JsonObject serialize(T area, JsonSerializationContext context) {
        var object = new JsonObject();
        object.addProperty("name", area.getName());
        object.addProperty("priority", area.getPriority());
        area.getOwner().ifPresent(owner -> object.add("owner", context.serialize(owner)));
        area.parent().ifPresent(parent -> object.addProperty("parent", parent));
        object.add("region", context.serialize(area.getRegion()));
        object.add("flags", context.serialize(area.getFlags()));
        return object;
    }
}
