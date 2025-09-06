package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public final class ByteFlag extends NumberFlag<Byte> {
    public ByteFlag(@NonNull Key key, @Nullable Byte defaultValue, @Nullable Byte protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public ByteFlag(@NonNull Key key, @NonNull Byte defaultValue, @Nullable Byte protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public ByteFlag(@NonNull Key key, @Nullable Byte defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public ByteFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Byte> getValueType() {
        return Byte.class;
    }

    @Override
    public @NonNull ArgumentType<Byte> getArgumentType() {
        return new ByteArgumentType();
    }

    @NullMarked
    private static final class ByteArgumentType implements CustomArgumentType.Converted<Byte, Integer> {
        @Override
        public Byte convert(Integer nativeType) {
            return nativeType.byteValue();
        }

        @Override
        public ArgumentType<Integer> getNativeType() {
            return IntegerArgumentType.integer(Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
    }
}
