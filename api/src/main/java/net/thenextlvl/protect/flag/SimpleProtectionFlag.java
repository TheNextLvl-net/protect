package net.thenextlvl.protect.flag;

import net.kyori.adventure.key.Key;

final class SimpleProtectionFlag extends SimpleFlag implements ProtectionFlag {
    private final boolean defaultValue;
    private final boolean protectedValue;
    private final boolean value;
    
    public SimpleProtectionFlag(final Key key, final boolean defaultValue, final boolean protectedValue) {
        this(key, defaultValue, protectedValue, defaultValue);
    }
    
    public SimpleProtectionFlag(final Key key, final boolean defaultValue, final boolean protectedValue, final boolean value) {
        super(key);
        this.defaultValue = defaultValue;
        this.protectedValue = protectedValue;
        this.value = value;
    }

    @Override
    public boolean getProtectedValue() {
        return protectedValue;
    }

    @Override
    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public ProtectionFlag withValue(final boolean value) {
        return new SimpleProtectionFlag(key(), defaultValue, protectedValue, value);
    }
}
