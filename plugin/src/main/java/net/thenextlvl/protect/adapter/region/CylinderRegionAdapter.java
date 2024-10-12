package net.thenextlvl.protect.adapter.region;

import com.google.gson.*;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.regions.CylinderRegion;

import java.lang.reflect.Type;

public class CylinderRegionAdapter implements JsonSerializer<CylinderRegion>, JsonDeserializer<CylinderRegion> {

    @Override
    public CylinderRegion deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        var object = element.getAsJsonObject();
        var center = context.<BlockVector3>deserialize(object.get("center"), BlockVector3.class);
        var radius = context.<Vector2>deserialize(object.get("radius"), Vector2.class);
        var max = context.<Integer>deserialize(object.get("max"), int.class);
        var min = context.<Integer>deserialize(object.get("min"), int.class);
        return new CylinderRegion(center, radius, min, max);
    }

    @Override
    public JsonElement serialize(CylinderRegion region, Type type, JsonSerializationContext context) {
        var object = new JsonObject();
        object.add("center", context.serialize(region.getCenter().toBlockPoint()));
        object.add("radius", context.serialize(region.getRadius()));
        object.addProperty("max", region.getMaximumY());
        object.addProperty("min", region.getMinimumY());
        return object;
    }
}
