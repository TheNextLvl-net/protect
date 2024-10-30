package net.thenextlvl.protect.flag;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public record CraftFlag<T>(
        @NonNull NamespacedKey key,
        @NonNull Class<? extends T> type,
        T defaultValue
) implements Flag<T> {

    @Override
    public int compareTo(@NonNull Flag<?> flag) {
        return key().compareTo(flag.key());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CraftFlag<?> craftFlag)) return false;
        return Objects.equals(key, craftFlag.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
