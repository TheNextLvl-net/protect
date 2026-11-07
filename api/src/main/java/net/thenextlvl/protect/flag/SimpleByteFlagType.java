package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.nbt.tag.ByteTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleByteFlagType(byte min, byte max) implements ByteFlagType {
    @Override
    public Class<Byte> type() {
        return Byte.class;
    }

    @Override
    public Tag encode(final Byte value) {
        return ByteTag.of(value);
    }

    @Override
    public Byte decode(final Tag tag) {
        return tag.getAsByte();
    }

    @Override
    public ArgumentType<Byte> argumentType() {
        return new ByteArgumentType(min, max);
    }

    private record ByteArgumentType(byte min, byte max) implements CustomArgumentType.Converted<Byte, Integer> {
        @Override
        public Byte convert(final Integer nativeType) {
            return nativeType.byteValue();
        }

        @Override
        public ArgumentType<Integer> getNativeType() {
            return IntegerArgumentType.integer(min, max);
        }
    }
}
