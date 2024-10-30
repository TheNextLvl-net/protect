package net.thenextlvl.protect.adapter.vector;

import com.google.gson.*;
import com.sk89q.worldedit.math.Vector3;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.Type;

@NullMarked
public class Vector3Adapter implements JsonSerializer<Vector3>, JsonDeserializer<Vector3> {

    @Override
    public Vector3 deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var split = element.getAsString().split(", ");
        var x = Double.parseDouble(split[0]);
        var y = Double.parseDouble(split[1]);
        var z = Double.parseDouble(split[2]);
        return Vector3.at(x, y, z);
    }

    @Override
    public JsonElement serialize(Vector3 vector, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(vector.x() + ", " + vector.y() + ", " + vector.z());
    }
}
