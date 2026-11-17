package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code short} values, optionally constrained to an inclusive range.
 *
 * @since 4.0.0
 */
public sealed interface ShortFlagType extends FlagType<Short> permits SimpleShortFlagType {
    /**
     * Creates a new short flag type that accepts any short value.
     *
     * @return a new short flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static ShortFlagType shortType() {
        return shortType(Short.MIN_VALUE);
    }

    /**
     * Creates a new short flag type with the given lower bound and no upper bound.
     *
     * @param min the inclusive minimum value
     * @return a new short flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static ShortFlagType shortType(final short min) {
        return shortType(min, Short.MAX_VALUE);
    }

    /**
     * Creates a new short flag type constrained to the given inclusive range.
     *
     * @param min the inclusive minimum value
     * @param max the inclusive maximum value
     * @return a new short flag type
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static ShortFlagType shortType(final short min, final short max) {
        return new SimpleShortFlagType(min, max);
    }
}
