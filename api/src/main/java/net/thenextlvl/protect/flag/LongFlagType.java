package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code long} values, optionally constrained to an inclusive range.
 *
 * @since 4.0.0
 */
public sealed interface LongFlagType extends FlagType<Long> permits SimpleLongFlagType {
    /**
     * Creates a new long flag type that accepts any long value.
     *
     * @return a new long flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static LongFlagType longType() {
        return longType(Long.MIN_VALUE);
    }

    /**
     * Creates a new long flag type with the given lower bound and no upper bound.
     *
     * @param min the inclusive minimum value
     * @return a new long flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static LongFlagType longType(final long min) {
        return longType(min, Long.MAX_VALUE);
    }

    /**
     * Creates a new long flag type constrained to the given inclusive range.
     *
     * @param min the inclusive minimum value
     * @param max the inclusive maximum value
     * @return a new long flag type
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static LongFlagType longType(final long min, final long max) {
        return new SimpleLongFlagType(min, max);
    }
}
