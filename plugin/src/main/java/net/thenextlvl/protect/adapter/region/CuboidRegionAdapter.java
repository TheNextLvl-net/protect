package net.thenextlvl.protect.adapter.region;

import com.google.gson.*;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

import java.lang.reflect.Type;

public class CuboidRegionAdapter implements JsonSerializer<CuboidRegion>, JsonDeserializer<CuboidRegion> {

    @Override
    public CuboidRegion deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        var object = element.getAsJsonObject();
        var pos1 = context.<BlockVector3>deserialize(object.get("pos1"), BlockVector3.class);
        var pos2 = context.<BlockVector3>deserialize(object.get("pos2"), BlockVector3.class);
        return new CuboidRegion(pos1, pos2);
    }

    @Override
    public JsonElement serialize(CuboidRegion region, Type type, JsonSerializationContext context) {
        var object = new JsonObject();
        object.add("pos1", context.serialize(region.getPos1()));
        object.add("pos2", context.serialize(region.getPos2()));
        return object;
    }
}
