package net.thenextlvl.protect.adapter.region;

import com.google.gson.*;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.EllipsoidRegion;

import java.lang.reflect.Type;

public class EllipsoidRegionAdapter implements JsonSerializer<EllipsoidRegion>, JsonDeserializer<EllipsoidRegion> {

    @Override
    public EllipsoidRegion deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        var object = element.getAsJsonObject();
        var center = context.<BlockVector3>deserialize(object.get("center"), BlockVector3.class);
        var radius = context.<Vector3>deserialize(object.get("radius"), Vector3.class);
        return new EllipsoidRegion(center, radius);
    }

    @Override
    public JsonElement serialize(EllipsoidRegion region, Type type, JsonSerializationContext context) {
        var object = new JsonObject();
        object.add("center", context.serialize(region.getCenter().toBlockPoint()));
        object.add("radius", context.serialize(region.getRadius()));
        return object;
    }
}
