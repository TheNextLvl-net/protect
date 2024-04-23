package net.thenextlvl.protect.flag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record CraftFlag<T>(@NotNull NamespacedKey key, @NotNull Class<? extends T> type, T defaultValue) implements Flag<T> {

    @Override
    public int compareTo(@NotNull Flag<?> flag) {
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
