package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.thenextlvl.nbt.tag.IntTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleIntegerFlagType(int min, int max) implements IntegerFlagType {
    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    public Tag encode(final Integer value) {
        return IntTag.of(value);
    }

    @Override
    public Integer decode(final Tag tag) {
        return tag.getAsInt();
    }

    @Override
    public ArgumentType<Integer> argumentType() {
        return IntegerArgumentType.integer(min, max);
    }
}
