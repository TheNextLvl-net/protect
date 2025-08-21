package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class BooleanFlag extends Flag<Boolean> {
    public BooleanFlag(@NonNull Key key, @NonNull Boolean defaultValue, @Nullable Boolean protectedValue) {
        super(key, defaultValue, protectedValue, false);
    }

    public BooleanFlag(@NonNull Key key, @NonNull Boolean defaultValue) {
        this(key, defaultValue, null);
    }

    @Override
    public @NonNull Boolean getDefaultValue() {
        return Objects.requireNonNull(super.getDefaultValue());
    }

    @Override
    public @NonNull Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @Override
    public @NonNull ArgumentType<Boolean> getArgumentType() {
        return BoolArgumentType.bool();
    }
}
