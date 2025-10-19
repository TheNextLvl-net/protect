package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record CraftProtectionFlag<T>(
        @NonNull Key key,
        @NonNull Class<? extends T> type,
        T defaultValue, T protectedValue
) implements ProtectionFlag<T> {

    @Override
    public int compareTo(@NonNull Flag<?> flag) {
        return key().compareTo(flag.key());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CraftProtectionFlag<?> craftFlag)) return false;
        return Objects.equals(key, craftFlag.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
