package net.thenextlvl.protect.api.adapter;

import com.google.gson.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.function.Function;

public class RegionAdapter implements JsonSerializer<Region>, JsonDeserializer<Region> {
    @Override
    public Region deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();
        World world = new BukkitWorld(Bukkit.getWorld(object.get("world").getAsString()));
        BlockVector3 min = deserializer.apply(object.get("min").getAsJsonObject());
        BlockVector3 max = deserializer.apply(object.get("max").getAsJsonObject());
        return new CuboidRegion(world, min, max);
    }

    @Override
    public JsonElement serialize(Region region, Type type, JsonSerializationContext context) {
        if (region.getWorld() == null) return JsonNull.INSTANCE;
        JsonObject object = new JsonObject();
        object.addProperty("world", region.getWorld().getName());
        object.add("min", serializer.apply(region.getMinimumPoint()));
        object.add("max", serializer.apply(region.getMaximumPoint()));
        return object;
    }

    private final Function<JsonObject, BlockVector3> deserializer = object -> {
        int x = object.get("x").getAsInt();
        int y = object.get("y").getAsInt();
        int z = object.get("z").getAsInt();
        return BlockVector3.at(x, y, z);
    };

    private final Function<BlockVector3, JsonObject> serializer = vector3 -> {
        JsonObject object = new JsonObject();
        object.addProperty("x", vector3.getX());
        object.addProperty("y", vector3.getY());
        object.addProperty("z", vector3.getZ());
        return object;
    };
}
