package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class FloatFlag extends NumberFlag<Float> {
    public FloatFlag(@NonNull Key key, @Nullable Float defaultValue, @Nullable Float protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public FloatFlag(@NonNull Key key, @NonNull Float defaultValue, @Nullable Float protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public FloatFlag(@NonNull Key key, @Nullable Float defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public FloatFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Float> getValueType() {
        return Float.class;
    }

    @Override
    public @NonNull ArgumentType<Float> getArgumentType() {
        return FloatArgumentType.floatArg();
    }
}
