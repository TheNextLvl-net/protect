package net.thenextlvl.protect.flag.collection;

import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class ListFlag<T> extends CollectionFlag<T, List<T>> {
    public ListFlag(@NonNull Key key, @NonNull List<T> defaultValue, @Nullable List<T> protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public ListFlag(@NonNull Key key, @NonNull List<T> defaultValue) {
        super(key, defaultValue);
    }
}
