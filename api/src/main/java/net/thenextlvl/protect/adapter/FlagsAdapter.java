package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.Flags;

import java.lang.reflect.Type;

public class FlagsAdapter implements JsonSerializer<Flags>, JsonDeserializer<Flags> {
    @Override
    public Flags deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var root = element.getAsJsonObject();
        Flags flags = new Flags();
        root.entrySet().forEach(entry -> {
            var flag = Flag.valueOf(entry.getKey());
            if (flag == null) return;
            var string = entry.getValue().getAsString();
            var value = flag.possibilities().get(string);
            if (value != null) flags.put(flag, value);
        });
        return flags;
    }

    @Override
    public JsonElement serialize(Flags flags, Type type, JsonSerializationContext context) {
        var root = new JsonObject();
        flags.forEach((flag, value) -> root.addProperty(flag.name(), flag.possibilities().name(value)));
        return root;
    }
}
