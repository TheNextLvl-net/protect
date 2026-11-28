package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import net.thenextlvl.nbt.tag.Tag;
import org.jetbrains.annotations.Contract;

/**
 * Describes how values of a flag are stored, parsed, and wrapped.
 *
 * @param <T> the type of the flag value
 * @since 4.0.0
 */
public interface FlagType<T> {
    /**
     * Retrieves the class of the values described by this type.
     *
     * @return the value class
     * @since 4.0.0
     */
    @Contract(pure = true)
    Class<T> type();

    /**
     * Encodes the given value into a tag for storage.
     *
     * @param value the value to encode
     * @return the encoded tag
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    Tag encode(T value);

    /**
     * Decodes a value from the given tag.
     *
     * @param tag the tag to decode
     * @return the decoded value
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    T decode(Tag tag);

    /**
     * Creates the command argument type used to parse values of this type.
     *
     * @return a new argument type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    ArgumentType<T> argumentType();
}
