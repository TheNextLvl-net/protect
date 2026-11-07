package net.thenextlvl.protect.lifecycle;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class CraftLifecycleEvent implements LifecycleEvent {
    private final Plugin owner;
    private volatile boolean valid = true;

    protected CraftLifecycleEvent(final Plugin owner) {
        this.owner = owner;
    }

    @Override
    public Plugin owner() {
        return owner;
    }

    protected void checkValid() throws IllegalStateException {
        Preconditions.checkState(valid, "Lifecycle event used outside of its handler");
    }

    void invalidate() {
        valid = false;
    }
}
