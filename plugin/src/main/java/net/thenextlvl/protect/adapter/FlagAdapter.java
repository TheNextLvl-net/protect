package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;

import java.lang.reflect.Type;

@RequiredArgsConstructor
public class FlagAdapter implements JsonSerializer<Flag<?>>, JsonDeserializer<Flag<?>> {
    private final ProtectPlugin plugin;

    @Override
    public JsonElement serialize(Flag<?> flag, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(flag.key().asString());
    }

    @Override
    public Flag<?> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var key = NamespacedKey.fromString(element.getAsString());
        return key != null ? plugin.flagRegistry().getFlag(key).orElse(null) : null;
    }
}
