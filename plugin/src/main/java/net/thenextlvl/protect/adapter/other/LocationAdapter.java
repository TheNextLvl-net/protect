package net.thenextlvl.protect.adapter.other;

import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
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
        var tag = CompoundTag.empty();
        tag.add("world", context.serialize(location.getWorld()));
        tag.add("x", location.getX());
        tag.add("y", location.getY());
        tag.add("z", location.getZ());
        tag.add("yaw", location.getYaw());
        tag.add("pitch", location.getPitch());
        return tag;
    }
}
