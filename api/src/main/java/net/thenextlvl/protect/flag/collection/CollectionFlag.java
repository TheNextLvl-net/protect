package net.thenextlvl.protect.flag.collection;

import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public abstract class CollectionFlag<E, T extends Collection<E>> extends Flag<T> {
    protected CollectionFlag(@NonNull Key key, @NonNull T defaultValue, @Nullable T protectedValue) {
        super(key, defaultValue, protectedValue, false);
    }

    protected CollectionFlag(@NonNull Key key, @NonNull T defaultValue) {
        this(key, defaultValue, null);
    }
}
