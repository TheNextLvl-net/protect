package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code float} values, optionally constrained to an inclusive range.
 *
 * @since 4.0.0
 */
public sealed interface FloatFlagType extends FlagType<Float> permits SimpleFloatFlagType {
    /**
     * Creates a new float flag type that accepts any finite float value.
     *
     * @return a new float flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static FloatFlagType floatType() {
        return floatType(-Float.MAX_VALUE);
    }

    /**
     * Creates a new float flag type with the given lower bound and no upper bound.
     *
     * @param min the inclusive minimum value
     * @return a new float flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static FloatFlagType floatType(final float min) {
        return floatType(min, Float.MAX_VALUE);
    }

    /**
     * Creates a new float flag type constrained to the given inclusive range.
     *
     * @param min the inclusive minimum value
     * @param max the inclusive maximum value
     * @return a new float flag type
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static FloatFlagType floatType(final float min, final float max) {
        return new SimpleFloatFlagType(min, max);
    }
}
