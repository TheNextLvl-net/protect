package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.serialization.TagDeserializationContext;
import net.thenextlvl.nbt.serialization.TagSerializationContext;
import net.thenextlvl.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CollectionFlag<E> extends SimpleFlag implements SerializableFlag<List<E>>, CommandFlag<E> {
    private final Type type;
    private final List<E> defaultValue;
    private final List<E> value;
    private final CommandFlag<E> elementFlag;

    public CollectionFlag(
            final Key key,
            final Type type,
            final Collection<E> defaultValue,
            final CommandFlag<E> elementFlag
    ) {
        this(key, type, defaultValue, defaultValue, elementFlag);
    }

    private CollectionFlag(
            final Key key,
            final Type type,
            final Collection<E> defaultValue,
            final Collection<E> value,
            final CommandFlag<E> elementFlag
    ) {
        super(key);
        this.type = type;
        this.defaultValue = List.copyOf(defaultValue);
        this.value = List.copyOf(value);
        this.elementFlag = elementFlag;
    }

    @Contract(pure = true)
    public List<E> getDefaultValue() {
        return defaultValue;
    }

    @Contract(pure = true)
    public List<E> getValue() {
        return value;
    }

    @Contract(value = "_ -> new", pure = true)
    public CollectionFlag<E> withValue(final Collection<E> value) {
        return new CollectionFlag<>(key(), type, defaultValue, value, elementFlag);
    }

    @SuppressWarnings("unchecked")
    public @Nullable List<E> cast(@Nullable final Object value) {
        if (value == null) return null;
        if (!(value instanceof final Collection<?> collection)) return (List<E>) value;
        final var copy = new ArrayList<E>(collection.size());
        for (final var element : collection) copy.add((E) element);
        return List.copyOf(copy);
    }

    public boolean supportsSet() {
        return false;
    }

    @Override
    public boolean supportsAdd() {
        return true;
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }

    @Override
    public ArgumentType<E> argumentType() {
        return elementFlag.argumentType();
    }

    public E resolveElement(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return elementFlag.resolve(context);
    }

    @Override
    public E resolve(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return resolveElement(context);
    }

    public String format(final Collection<?> value) {
        return value.toString();
    }

    @Override
    public List<E> deserialize(final Tag tag, final TagDeserializationContext context) {
        return context.deserialize(tag, type);
    }

    @Override
    public Tag serialize(final List<E> value, final TagSerializationContext context) {
        return context.serialize(value, type);
    }
}
