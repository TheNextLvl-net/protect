package net.thenextlvl.protect.adapters;

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
public final class LocationAdapter implements TagAdapter<Location> {
    @Override
    public Location deserialize(final Tag tag, final TagDeserializationContext context) throws ParserException {
        final var compound = tag.getAsCompound();
        final var world = context.deserialize(compound.get("world"), World.class);
        final var x = compound.get("x").getAsDouble();
        final var y = compound.get("y").getAsDouble();
        final var z = compound.get("z").getAsDouble();
        final var yaw = compound.get("yaw").getAsFloat();
        final var pitch = compound.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public Tag serialize(final Location location, final TagSerializationContext context) throws ParserException {
        return CompoundTag.builder()
                .put("world", context.serialize(location.getWorld()))
                .put("x", location.getX())
                .put("y", location.getY())
                .put("z", location.getZ())
                .put("yaw", location.getYaw())
                .put("pitch", location.getPitch())
                .build();
    }
}
