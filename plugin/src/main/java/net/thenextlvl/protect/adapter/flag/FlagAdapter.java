package net.thenextlvl.protect.adapter.flag;

import com.google.gson.*;
import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@RequiredArgsConstructor
@FieldsAreNotNullByDefault
@ParametersAreNotNullByDefault
public class FlagAdapter implements JsonSerializer<Flag<?>>, JsonDeserializer<Flag<?>> {
    private final ProtectPlugin plugin;

    @Override
    public @NotNull JsonElement serialize(Flag<?> flag, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(flag.key().asString());
    }

    @Override
    public Flag<?> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var key = NamespacedKey.fromString(element.getAsString());
        return key != null ? plugin.flagRegistry().getFlag(key).orElse(null) : null;
    }
}
