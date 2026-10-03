package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;

record SimpleProtectionFlagInstance<T>(
        Key key,
        FlagType<T> flagType,
        T defaultValue,
        T protectedValue
) implements ProtectionFlagInstance<T> {
}
