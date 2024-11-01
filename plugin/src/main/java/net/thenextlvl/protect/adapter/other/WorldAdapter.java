package net.thenextlvl.protect.adapter.other;

import core.nbt.serialization.ParserException;
import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class WorldAdapter implements TagAdapter<World> {
    private final Server server;

    public WorldAdapter(Server server) {
        this.server = server;
    }

    @Override
    public World deserialize(Tag tag, TagDeserializationContext context) throws ParserException {
        var namespace = context.deserialize(tag, NamespacedKey.class);
        var world = server.getWorld(namespace);
        if (world != null) return world;
        throw new ParserException("World not found: " + namespace);
    }

    @Override
    public Tag serialize(World object, TagSerializationContext context) throws ParserException {
        return context.serialize(object.getKey());
    }
}
