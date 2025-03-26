package net.thenextlvl.protect.adapter.other;

import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class FlagsAdapter implements TagAdapter<Map<Flag<?>, @Nullable Object>> {
    private final ProtectPlugin plugin;

    public FlagsAdapter(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public Map<Flag<?>, @Nullable Object> deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var flags = new HashMap<Flag<?>, @Nullable Object>();
        compound.forEach((key, value) -> {
            var namespace = Key.key(key);
            var flag = plugin.flagRegistry().getFlag(namespace).orElse(null);
            if (flag != null) flags.put(flag, context.deserialize(value, flag.type()));
            else plugin.getComponentLogger().error("Unknown flag: {}", key);
        });
        return flags;
    }

    @Override
    public Tag serialize(Map<Flag<?>, @Nullable Object> flags, TagSerializationContext context) {
        var tag = new CompoundTag();
        flags.forEach((flag, value) -> {
            if (value == null) return;
            var serialized = context.serialize(value, flag.type());
            tag.add(flag.key().asString(), serialized);
        });
        return tag;
    }
}
