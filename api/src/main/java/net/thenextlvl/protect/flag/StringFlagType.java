package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import org.jetbrains.annotations.Contract;

/**
 * A {@link FlagType} for {@link String} values.
 *
 * @since 4.0.0
 */
public sealed interface StringFlagType extends FlagType<String> permits SimpleStringFlagType {
    /**
     * Creates a new string flag type that parses a {@link StringType#QUOTABLE_PHRASE quotable phrase}.
     *
     * @return a new string flag type
     * @since 4.0.0
     */
    @Contract(value = " -> new", pure = true)
    static StringFlagType stringType() {
        return stringType(StringType.QUOTABLE_PHRASE);
    }

    /**
     * Creates a new string flag type that parses strings of the given kind.
     *
     * @param kind how the string is read from a command
     * @return a new string flag type
     * @since 4.0.0
     */
    @Contract(value = "_ -> new", pure = true)
    static StringFlagType stringType(final StringType kind) {
        return new SimpleStringFlagType(kind);
    }
}
