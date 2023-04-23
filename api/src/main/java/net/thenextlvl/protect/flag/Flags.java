package net.thenextlvl.protect.flag;

import core.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public class Flags extends HashMap<Flag<?>, Object> {
    public Flags(Map<? extends Flag<?>, ?> map) {
        super(map);
    }
}
