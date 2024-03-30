package net.thenextlvl.protect.flag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public record CraftFlag<T>(@NotNull NamespacedKey key, @NotNull Class<? extends T> type, T defaultValue) implements Flag<T> {

    @Override
    public int compareTo(@NotNull Flag<?> flag) {
        return key().compareTo(flag.key());
    }
}
