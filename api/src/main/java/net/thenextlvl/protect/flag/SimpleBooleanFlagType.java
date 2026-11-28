package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.thenextlvl.nbt.tag.ByteTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleBooleanFlagType() implements BooleanFlagType {
    public static final BooleanFlagType INSTANCE = new SimpleBooleanFlagType();

    @Override
    public Class<Boolean> type() {
        return Boolean.class;
    }

    @Override
    public Tag encode(final Boolean value) {
        return ByteTag.of(value);
    }

    @Override
    public Boolean decode(final Tag tag) {
        return tag.getAsBoolean();
    }

    @Override
    public ArgumentType<Boolean> argumentType() {
        return BoolArgumentType.bool();
    }
}
