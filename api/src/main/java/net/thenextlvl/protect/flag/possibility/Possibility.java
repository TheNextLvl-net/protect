package net.thenextlvl.protect.flag.possibility;

import core.annotation.ParametersAreNonnullByDefault;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = false)
@ParametersAreNonnullByDefault
public class Possibility<T> {
    private final String name;
    private final T value;

    public Possibility(T value) {
        this(String.valueOf(value), value);
    }
}
