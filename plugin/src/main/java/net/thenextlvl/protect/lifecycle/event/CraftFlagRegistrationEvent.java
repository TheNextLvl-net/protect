package net.thenextlvl.protect.lifecycle.event;

import net.thenextlvl.protect.flag.CraftFlagRegistry;
import net.thenextlvl.protect.flag.FlagInstance;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.lifecycle.CraftLifecycleEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CraftFlagRegistrationEvent extends CraftLifecycleEvent implements FlagRegistrationEvent {
    private final CraftFlagRegistry registry;

    public CraftFlagRegistrationEvent(final Plugin owner, final CraftFlagRegistry registry) {
        super(owner);
        this.registry = registry;
    }

    @Override
    public FlagRegistry registry() {
        checkValid();
        return registry;
    }

    @Override
    public <F extends FlagInstance<?>> F register(final F flag) throws IllegalStateException {
        checkValid();
        return registry.register(owner(), flag);
    }
}
