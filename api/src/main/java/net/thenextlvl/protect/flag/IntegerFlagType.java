package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code int} values, optionally constrained to an inclusive range.
 *
 * @since 4.0.0
 */
public sealed interface IntegerFlagType extends FlagType<Integer> permits SimpleIntegerFlagType {
    /**
     * Creates a new int flag type that accepts any int value.
     *
     * @return a new int flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static IntegerFlagType integerType() {
        return integerType(Integer.MIN_VALUE);
    }

    /**
     * Creates a new int flag type with the given lower bound and no upper bound.
     *
     * @param min the inclusive minimum value
     * @return a new int flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static IntegerFlagType integerType(final int min) {
        return integerType(min, Integer.MAX_VALUE);
    }

    /**
     * Creates a new int flag type constrained to the given inclusive range.
     *
     * @param min the inclusive minimum value
     * @param max the inclusive maximum value
     * @return a new int flag type
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static IntegerFlagType integerType(final int min, final int max) {
        return new SimpleIntegerFlagType(min, max);
    }
}
