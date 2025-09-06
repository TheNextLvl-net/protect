package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class DoubleFlag extends NumberFlag<Double> {
    public DoubleFlag(@NonNull Key key, @Nullable Double defaultValue, @Nullable Double protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public DoubleFlag(@NonNull Key key, @NonNull Double defaultValue, @Nullable Double protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public DoubleFlag(@NonNull Key key, @Nullable Double defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public DoubleFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Double> getValueType() {
        return Double.class;
    }

    @Override
    public @NonNull ArgumentType<Double> getArgumentType() {
        return DoubleArgumentType.doubleArg();
    }
}
