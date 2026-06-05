package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;

public abstract class SimpleFlag implements Flag {
    private final Key key;

    public SimpleFlag(final Key key) {
        this.key = key;
    }

    @Override
    public final Key key() {
        return key;
    }

    @Override
    public boolean is(final Flag flag) {
        return flag.key().equals(key);
    }
}
