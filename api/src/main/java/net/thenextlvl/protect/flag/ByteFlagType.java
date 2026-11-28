package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code byte} values, optionally constrained to an inclusive range.
 *
 * @since 4.0.0
 */
public sealed interface ByteFlagType extends FlagType<Byte> permits SimpleByteFlagType {
    /**
     * Creates a new byte flag type that accepts any byte value.
     *
     * @return a new byte flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static ByteFlagType byteType() {
        return byteType(Byte.MIN_VALUE);
    }

    /**
     * Creates a new byte flag type with the given lower bound and no upper bound.
     *
     * @param min the inclusive minimum value
     * @return a new byte flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static ByteFlagType byteType(final byte min) {
        return byteType(min, Byte.MAX_VALUE);
    }

    /**
     * Creates a new byte flag type constrained to the given inclusive range.
     *
     * @param min the inclusive minimum value
     * @param max the inclusive maximum value
     * @return a new byte flag type
     * @since 4.0.0
     */
    @Contract(value = "_, _ -> new", pure = true)
    static ByteFlagType byteType(final byte min, final byte max) {
        return new SimpleByteFlagType(min, max);
    }
}
