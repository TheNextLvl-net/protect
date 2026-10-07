package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.Nullable;

record SimpleFlagInstance<T>(Key key, FlagType<T> flagType, @Nullable T defaultValue) implements FlagInstance<T> {
}
