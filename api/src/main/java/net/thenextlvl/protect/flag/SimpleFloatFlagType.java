package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.thenextlvl.nbt.tag.FloatTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleFloatFlagType(float min, float max) implements FloatFlagType {
    @Override
    public Class<Float> type() {
        return Float.class;
    }

    @Override
    public Tag encode(final Float value) {
        return FloatTag.of(value);
    }

    @Override
    public Float decode(final Tag tag) {
        return tag.getAsFloat();
    }

    @Override
    public ArgumentType<Float> argumentType() {
        return FloatArgumentType.floatArg(min, max);
    }
}
