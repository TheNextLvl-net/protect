package net.thenextlvl.protect.flag;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public record CraftFlag<T>(@NotNull NamespacedKey key, T defaultValue) implements Flag<T> {
}
