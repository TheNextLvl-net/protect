package net.thenextlvl.protect.adapter.other;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.ParserException;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class WorldAdapter implements TagAdapter<World> {
    private final Server server;

    public WorldAdapter(Server server) {
        this.server = server;
    }

    @Override
    public World deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var namespace = context.deserialize(tag, Key.class);
        var world = server.getWorld(namespace);
        if (world != null) return world;
        throw new ParserException("World not found: " + namespace);
    }

    @Override
    public Tag serialize(World object, TagSerializationContext context) throws ParserException {
        return context.serialize(object.getKey());
    }
}
