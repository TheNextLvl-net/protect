package net.thenextlvl.protect.api.flag;

import lombok.NoArgsConstructor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@ParametersAreNonnullByDefault
public class Flags extends HashMap<Flag<?>, Object> {
    public Flags(Map<? extends Flag<?>, ?> map) {
        super(map);
    }
}
