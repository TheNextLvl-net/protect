package net.thenextlvl.protect.adapter.flag;

import core.nbt.serialization.TagAdapter;
import core.nbt.serialization.TagDeserializationContext;
import core.nbt.serialization.TagSerializationContext;
import core.nbt.tag.CompoundTag;
import core.nbt.tag.Tag;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@NullMarked
@RequiredArgsConstructor
public class FlagsAdapter implements TagAdapter<Map<Flag<?>, @Nullable Object>> {
    private final ProtectPlugin plugin;

    @Override
    public Map<Flag<?>, @Nullable Object> deserialize(Tag tag, TagDeserializationContext context) {
        var compound = tag.getAsCompound();
        var flags = new LinkedHashMap<Flag<?>, @Nullable Object>();
        compound.forEach((key, value) -> {
            var namespace = NamespacedKey.fromString(key);
            var flag = namespace != null ? plugin.flagRegistry().getFlag(namespace).orElse(null) : null;
            if (flag != null) flags.put(flag, context.deserialize(value, flag.type()));
            else plugin.getComponentLogger().error("Unknown flag: {}", key);
        });
        return flags;
    }

    @Override
    public Tag serialize(Map<Flag<?>, @Nullable Object> flags, TagSerializationContext context) {
        var tag = new CompoundTag();
        flags.forEach((flag, value) -> tag.add(flag.key().asString(), context.serialize(value, flag.type())));
        return tag;
    }
}
