package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for flags that store typed values.
 *
 * @param <T> the value type
 */
public abstract class ValueFlag<T> extends SimpleFlag implements SerializableFlag<T> {
    protected final @Nullable T defaultValue;
    protected final @Nullable T value;
    protected final Type type;

    protected ValueFlag(final Key key, final Type type, @Nullable final T defaultValue) {
        this(key, type, defaultValue, defaultValue);
    }

    protected ValueFlag(final Key key, final Type type, @Nullable final T defaultValue, @Nullable final T value) {
        super(key);
        this.defaultValue = defaultValue;
        this.value = value;
        this.type = type;
    }

    @Contract(pure = true)
    public Optional<T> defaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Contract(pure = true)
    public final Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    @Contract(value = "_ -> new", pure = true)
    public abstract ValueFlag<T> withValue(@Nullable final T value);

    @Contract(pure = true)
    public String format(@Nullable final T value) {
        return String.valueOf(value);
    }

    @Override
    public T deserialize(final Tag tag, final TagDeserializationContext context) {
        return context.deserialize(tag, type);
    }

    @Override
    public Tag serialize(final T value, final TagSerializationContext context) {
        return context.serialize(value, type);
    }

    @SuppressWarnings("unchecked")
    public @Nullable T cast(@Nullable final Object value) {
        return (T) value;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final Flag flag)) return false;
        return Objects.equals(key(), flag.key());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(key());
    }
}
