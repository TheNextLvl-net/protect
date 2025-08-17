package net.thenextlvl.protect.flag;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class Flag<T> implements Keyed, Comparable<@NonNull Flag<?>> {
    private final @NonNull Key key;
    private final T defaultValue;
    private final T protectedValue;
    private final boolean nullable;

    protected Flag(@NonNull Key key, @Nullable T defaultValue, @Nullable T protectedValue, boolean nullable) {
        Preconditions.checkArgument(nullable || defaultValue != null, "Default value must be non-null if flag is not nullable");
        this.key = key;
        this.defaultValue = defaultValue;
        this.protectedValue = protectedValue;
        this.nullable = nullable;
    }

    protected Flag(@NonNull Key key, @NonNull T defaultValue, @Nullable T protectedValue) {
        this(key, defaultValue, protectedValue, false);
    }

    protected Flag(@NonNull Key key, @Nullable T defaultValue, boolean nullable) {
        this(key, defaultValue, null, nullable);
    }

    protected Flag(@NonNull Key key) {
        this(key, null, false);
    }

    @Override
    public @NonNull Key key() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getProtectedValue() {
        return protectedValue;
    }

    public boolean isNullable() {
        return nullable;
    }

    @Override
    public int compareTo(@NonNull Flag<?> flag) {
        return key.compareTo(flag.key());
    }
}
