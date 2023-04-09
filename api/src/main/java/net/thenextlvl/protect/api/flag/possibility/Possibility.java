package net.thenextlvl.protect.api.flag.possibility;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.ParametersAreNonnullByDefault;

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
