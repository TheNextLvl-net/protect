package net.thenextlvl.protect.flag.possibility;

import core.annotation.ParametersAreNonnullByDefault;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a map that holds the name of a possibility
 *
 * @param <T> the type of the values hold by the map
 */
@NoArgsConstructor
@ParametersAreNonnullByDefault
public class Possibilities<T> extends HashMap<String, T> {
    public static Possibilities<Boolean> BOOLEAN_VALUES = of(true, false);

    public Possibilities(Map<String, T> map) {
        super(map);
    }

    @SafeVarargs
    public static <T> Possibilities<T> of(T... possibilities) {
        Possibility<T>[] array = new Possibility[possibilities.length];
        for (int i = 0; i < possibilities.length; i++) array[i] = new Possibility<>(possibilities[i]);
        return of(array);
    }

    @SafeVarargs
    public static <T> Possibilities<T> of(Possibility<T>... possibilities) {
        HashMap<String, T> map = new HashMap<>();
        for (Possibility<T> possibility : possibilities) map.put(possibility.getName(), possibility.getValue());
        return new Possibilities<>(map);
    }

    /**
     * @param value the value
     * @return the name of the value
     */
    public String name(Object value) {
        for (String s : keySet()) if (get(s).equals(value)) return s;
        return String.valueOf(value);
    }
}
