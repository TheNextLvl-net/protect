package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
public class FlagsAdapter implements JsonSerializer<Map<Flag<?>, Object>>, JsonDeserializer<Map<Flag<?>, Object>> {
    private final ProtectPlugin plugin;

    @Override
    public JsonObject serialize(Map<Flag<?>, Object> flags, Type type, JsonSerializationContext context) {
        var object = new JsonObject();
        flags.forEach((flag, o) -> object.add(flag.key().asString(), context.serialize(o, flag.type())));
        return object;
    }

    @Override
    public Map<Flag<?>, Object> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var object = element.getAsJsonObject();
        var flags = new LinkedHashMap<Flag<?>, Object>();
        object.entrySet().forEach(entry -> {
            var key = NamespacedKey.fromString(entry.getKey());
            var flag = key != null ? plugin.flagRegistry().getFlag(key).orElse(null) : null;
            if (flag != null) flags.put(flag, context.deserialize(entry.getValue(), flag.type()));
            else plugin.getComponentLogger().error("Unknown flag: {}", entry.getKey());
        });
        return flags;
    }
}
