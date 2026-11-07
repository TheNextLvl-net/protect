package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.thenextlvl.nbt.tag.LongTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleLongFlagType(long min, long max) implements LongFlagType {
    @Override
    public Class<Long> type() {
        return Long.class;
    }

    @Override
    public Tag encode(final Long value) {
        return LongTag.of(value);
    }

    @Override
    public Long decode(final Tag tag) {
        return tag.getAsLong();
    }

    @Override
    public ArgumentType<Long> argumentType() {
        return LongArgumentType.longArg(min, max);
    }
}
