package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import core.paper.command.WrappedArgumentType;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class ShortFlag extends NumberFlag<Short> {
    public ShortFlag(@NonNull Key key, @Nullable Short defaultValue, @Nullable Short protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public ShortFlag(@NonNull Key key, @NonNull Short defaultValue, @Nullable Short protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public ShortFlag(@NonNull Key key, @Nullable Short defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public ShortFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Short> getValueType() {
        return Short.class;
    }

    @Override
    public @NonNull ArgumentType<Short> getArgumentType() {
        return new WrappedArgumentType<>(
                IntegerArgumentType.integer(Short.MIN_VALUE, Short.MAX_VALUE),
                (reader, type) -> type.shortValue(),
                (context, builder) -> builder.buildFuture()
        );
    }
}
