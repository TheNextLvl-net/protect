package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@code boolean} values.
 *
 * @since 4.0.0
 */
public sealed interface BooleanFlagType extends FlagType<Boolean> permits SimpleBooleanFlagType {
    /**
     * Returns the boolean flag type.
     *
     * @return the boolean flag type
     * @since 4.0.0
     */
    @Contract(pure = true)
    static BooleanFlagType booleanType() {
        return SimpleBooleanFlagType.INSTANCE;
    }
}
