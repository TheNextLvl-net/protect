package net.thenextlvl.protect.flag.standard;

import com.mojang.brigadier.arguments.ArgumentType;
import core.paper.command.argument.EnumArgumentType;
import core.paper.command.argument.codec.EnumStringCodec;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class EnumFlag<T extends Enum<T>> extends Flag<T> {
    private final @NonNull Class<T> type;
    
    public EnumFlag(@NonNull Key key, @NonNull Class<T> type, @Nullable T defaultValue, @Nullable T protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
        this.type = type;
    }

    public EnumFlag(@NonNull Key key, @NonNull Class<T> type, @NonNull T defaultValue, @Nullable T protectedValue) {
        super(key, defaultValue, protectedValue);
        this.type = type;
    }

    public EnumFlag(@NonNull Key key, @NonNull Class<T> type, @Nullable T defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
        this.type = type;
    }

    public EnumFlag(@NonNull Key key, @NonNull Class<T> type) {
        super(key);
        this.type = type;
    }

    @Override
    public @NonNull Class<T> getValueType() {
        return type;
    }

    @Override
    public @NonNull ArgumentType<T> getArgumentType() {
        return EnumArgumentType.of(getValueType(), EnumStringCodec.lowerHyphen());
    }
}
