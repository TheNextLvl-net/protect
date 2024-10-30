package net.thenextlvl.protect.adapter.vector;

import com.google.gson.*;
import com.sk89q.worldedit.math.Vector2;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

@NullMarked
public class Vector2Adapter implements JsonSerializer<Vector2>, JsonDeserializer<Vector2> {

    @Override
    public Vector2 deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var split = element.getAsString().split(", ");
        var x = Double.parseDouble(split[0]);
        var z = Double.parseDouble(split[1]);
        return Vector2.at(x, z);
    }

    @Override
    public JsonElement serialize(Vector2 vector, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(vector.x() + ", " + vector.z());
    }
}
