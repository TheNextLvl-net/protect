package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.nbt.tag.ShortTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleShortFlagType(short min, short max) implements ShortFlagType {
    @Override
    public Class<Short> type() {
        return Short.class;
    }

    @Override
    public Tag encode(final Short value) {
        return ShortTag.of(value);
    }

    @Override
    public Short decode(final Tag tag) {
        return tag.getAsShort();
    }

    @Override
    public ArgumentType<Short> argumentType() {
        return new ShortArgumentType(min, max);
    }

    private record ShortArgumentType(short min, short max) implements CustomArgumentType.Converted<Short, Integer> {
        @Override
        public Short convert(final Integer nativeType) {
            return nativeType.shortValue();
        }

        @Override
        public ArgumentType<Integer> getNativeType() {
            return IntegerArgumentType.integer(min, max);
        }
    }
}
