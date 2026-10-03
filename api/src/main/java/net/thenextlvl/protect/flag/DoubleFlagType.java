package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code double} values, optionally constrained to an inclusive range.
 *
 * @since 4.0.0
 */
public sealed interface DoubleFlagType extends FlagType<Double> permits SimpleDoubleFlagType {
    /**
     * Creates a new double flag type that accepts any finite double value.
     *
     * @return a new double flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static DoubleFlagType doubleType() {
        return doubleType(-Double.MAX_VALUE);
    }

    /**
     * Creates a new double flag type with the given lower bound and no upper bound.
     *
     * @param min the inclusive minimum value
     * @return a new double flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static DoubleFlagType doubleType(final double min) {
        return doubleType(min, Double.MAX_VALUE);
    }

    /**
     * Creates a new double flag type constrained to the given inclusive range.
     *
     * @param min the inclusive minimum value
     * @param max the inclusive maximum value
     * @return a new double flag type
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static DoubleFlagType doubleType(final double min, final double max) {
        return new SimpleDoubleFlagType(min, max);
    }
}
