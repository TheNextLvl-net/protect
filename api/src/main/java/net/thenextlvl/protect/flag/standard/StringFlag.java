package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class StringFlag extends Flag<String> {
    public StringFlag(@NonNull Key key, @Nullable String defaultValue, @Nullable String protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public StringFlag(@NonNull Key key, @NonNull String defaultValue, @Nullable String protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public StringFlag(@NonNull Key key, @Nullable String defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public StringFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<String> getValueType() {
        return String.class;
    }

    @Override
    public @NonNull ArgumentType<String> getArgumentType() {
        return StringArgumentType.string();
    }
}
