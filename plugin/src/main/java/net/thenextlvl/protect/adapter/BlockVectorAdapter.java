package net.thenextlvl.protect.adapter;

import com.google.gson.*;
import com.sk89q.worldedit.math.BlockVector3;

import java.lang.reflect.Type;

public class BlockVectorAdapter implements JsonSerializer<BlockVector3>, JsonDeserializer<BlockVector3> {

    @Override
    public BlockVector3 deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var split = element.getAsString().split(", ");
        var x = Integer.parseInt(split[0]);
        var y = Integer.parseInt(split[1]);
        var z = Integer.parseInt(split[2]);
        return BlockVector3.at(x, y, z);
    }

    @Override
    public JsonElement serialize(BlockVector3 vector, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(vector.getX() + ", " + vector.getY() + ", " + vector.getZ());
    }
}
