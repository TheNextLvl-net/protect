package net.thenextlvl.protect.flag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record CraftProtectionFlag<T>(
        @NotNull NamespacedKey key,
        @NotNull Class<? extends T> type,
        T defaultValue, T protectedValue
) implements ProtectionFlag<T> {

    @Override
    public int compareTo(@NotNull Flag<?> flag) {
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
