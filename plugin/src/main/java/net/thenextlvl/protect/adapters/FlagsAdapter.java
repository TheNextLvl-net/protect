package net.thenextlvl.protect.adapters;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public final class FlagsAdapter implements TagAdapter<Map<Flag<?>, @Nullable Object>> {
    private final ProtectPlugin plugin;

    public FlagsAdapter(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public Map<Flag<?>, @Nullable Object> deserialize(final Tag tag, final TagDeserializationContext context) {
        final var compound = tag.getAsCompound();
        final var flags = new HashMap<Flag<?>, @Nullable Object>();
        compound.forEach((key, value) -> {
            final var namespace = Key.key(key);
            final var flag = plugin.flagRegistry().getFlag(namespace).orElse(null);
            if (flag != null) flags.put(flag, context.deserialize(value, flag.type()));
            else plugin.getComponentLogger().error("Unknown flag: {}", key);
        });
        return flags;
    }

    @Override
    public Tag serialize(final Map<Flag<?>, @Nullable Object> flags, final TagSerializationContext context) {
        final var tag = CompoundTag.builder();
        flags.forEach((flag, value) -> {
            if (value == null) return;
            final var serialized = context.serialize(value, flag.type());
            tag.put(flag.key().asString(), serialized);
        });
        return tag.build();
    }
}
