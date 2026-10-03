package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for constants of an enum.
 * <p>
 * Values are stored by their {@link Enum#name() name}.
 * In commands, constants are written in lower case with underscores replaced by hyphens,
 * for example {@code SOME_VALUE} becomes {@code some-value}.
 *
 * @param <E> the enum type
 * @since 4.0.0
 */
public sealed interface EnumFlagType<E extends Enum<E>> extends FlagType<E> permits SimpleEnumFlagType {
    /**
     * Creates a new enum flag type for the given enum class.
     *
     * @param type the enum class
     * @param <E>  the enum type
     * @return a new enum flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static <E extends Enum<E>> EnumFlagType<E> enumType(final Class<E> type) {
        return new SimpleEnumFlagType<>(type);
    }
}
