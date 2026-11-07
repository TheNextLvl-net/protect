package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.thenextlvl.nbt.tag.DoubleTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleDoubleFlagType(double min, double max) implements DoubleFlagType {
    @Override
    public Class<Double> type() {
        return Double.class;
    }

    @Override
    public Tag encode(final Double value) {
        return DoubleTag.of(value);
    }

    @Override
    public Double decode(final Tag tag) {
        return tag.getAsDouble();
    }

    @Override
    public ArgumentType<Double> argumentType() {
        return DoubleArgumentType.doubleArg(min, max);
    }
}
