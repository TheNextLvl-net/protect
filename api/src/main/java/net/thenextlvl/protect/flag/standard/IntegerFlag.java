package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class IntegerFlag extends NumberFlag<Integer> {
    public IntegerFlag(@NonNull Key key, @Nullable Integer defaultValue, @Nullable Integer protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public IntegerFlag(@NonNull Key key, @NonNull Integer defaultValue, @Nullable Integer protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public IntegerFlag(@NonNull Key key, @Nullable Integer defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public IntegerFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Integer> getValueType() {
        return Integer.class;
    }

    @Override
    public @NonNull ArgumentType<Integer> getArgumentType() {
        return IntegerArgumentType.integer();
    }
}
