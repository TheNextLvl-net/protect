package net.thenextlvl.protect.flag.collection;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public abstract class SetFlag<T> extends CollectionFlag<T, Set<T>> {
    public SetFlag(@NonNull Key key, @NonNull Set<T> defaultValue, @Nullable Set<T> protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public SetFlag(@NonNull Key key, @NonNull Set<T> defaultValue) {
        super(key, defaultValue);
    }
}
