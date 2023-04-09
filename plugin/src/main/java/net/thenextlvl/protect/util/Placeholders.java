package net.thenextlvl.protect.util;

import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.Protect;

public class Placeholders {
    public static void init(Protect plugin) {
        plugin.formatter().registry().register(Placeholder.of("prefix", Messages.PREFIX.message()));
    }
}
