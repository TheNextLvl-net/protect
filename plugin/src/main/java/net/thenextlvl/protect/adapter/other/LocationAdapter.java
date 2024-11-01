package net.thenextlvl.protect.adapter.other;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LocationAdapter implements TagAdapter<Location> {
    @Override
    public Location deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var compound = tag.getAsCompound();
        var world = context.deserialize(compound.get("world"), World.class);
        var x = compound.get("x").getAsDouble();
        var y = compound.get("y").getAsDouble();
        var z = compound.get("z").getAsDouble();
        var yaw = compound.get("yaw").getAsFloat();
        var pitch = compound.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public Tag serialize(Location location, TagSerializationContext context) throws ParserException {
        var tag = new CompoundTag();
        tag.add("world", context.serialize(location.getWorld(), World.class));
        tag.add("x", location.getX());
        tag.add("y", location.getY());
        tag.add("z", location.getZ());
        tag.add("yaw", location.getYaw());
        tag.add("pitch", location.getPitch());
        return tag;
    }
}
