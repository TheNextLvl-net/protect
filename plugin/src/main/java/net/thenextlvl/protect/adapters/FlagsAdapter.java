package net.thenextlvl.protect.adapters;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.TagAdapter;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.nbt.tag.Tag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.ValueFlag;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public final class FlagsAdapter implements TagAdapter<Set<Flag>> {
    private final ProtectPlugin plugin;

    public FlagsAdapter(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public Set<Flag> deserialize(final Tag tag, final TagDeserializationContext context) {
        final var compound = tag.getAsCompound();
        final var flags = new HashSet<Flag>();
        compound.forEach((key, value) -> {
            final var namespace = Key.key(key);
            final var flag = plugin.flagRegistry().getFlag(namespace).orElse(null);
            if (flag instanceof final ValueFlag<?> valueFlag) flags.add(deserialize(valueFlag, value, context));
            else if (flag != null) plugin.getComponentLogger().error("Flag is not serializable: {}", key);
            else plugin.getComponentLogger().error("Unknown flag: {}", key);
        });
        return flags;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Tag serialize(final Set<Flag> flags, final TagSerializationContext context) {
        final var tag = CompoundTag.builder();
        flags.forEach(flag -> {
            if (!(flag instanceof final ValueFlag valueFlag) || !valueFlag.hasValue()) return;
            final var serialize = valueFlag.serialize(valueFlag.value(), context);
            if (serialize == null) return;
            tag.put(flag.key().asString(), serialize);
        });
        return tag.build();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Flag deserialize(final ValueFlag flag, final Tag tag, final TagDeserializationContext context) {
        return flag.withValue(flag.deserialize(tag, context));
    }
}
