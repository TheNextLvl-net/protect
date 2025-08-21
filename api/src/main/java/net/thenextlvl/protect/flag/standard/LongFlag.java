package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class LongFlag extends NumberFlag<Long> {
    public LongFlag(@NonNull Key key, @Nullable Long defaultValue, @Nullable Long protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public LongFlag(@NonNull Key key, @NonNull Long defaultValue, @Nullable Long protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public LongFlag(@NonNull Key key, @Nullable Long defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public LongFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Long> getValueType() {
        return Long.class;
    }

    @Override
    public @NonNull ArgumentType<Long> getArgumentType() {
        return LongArgumentType.longArg();
    }
}
