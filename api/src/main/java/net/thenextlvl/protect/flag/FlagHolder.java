package net.thenextlvl.protect.flag;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

@ApiStatus.NonExtendable
public interface FlagHolder<T extends Flag> {
    @Contract(pure = true)
    Flag flag();
}
