package net.thenextlvl.protect.lifecycle;

import com.google.common.base.Preconditions;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@NullMarked
public final class CraftLifecycleManager implements LifecycleManager {
    private final Map<LifecycleEventType<?>, List<Registration<?>>> handlers = new HashMap<>();
    private final Set<LifecycleEventType<?>> fired = new HashSet<>();
    private final ProtectPlugin plugin;

    public CraftLifecycleManager(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    private record Registration<E extends LifecycleEvent>(Plugin owner, LifecycleEventHandler<? super E> handler) {
    }

    @Override
    public synchronized <E extends LifecycleEvent> void registerHandler(final Plugin owner, final LifecycleEventType<E> type, final LifecycleEventHandler<? super E> handler) throws IllegalStateException {
        Preconditions.checkState(!fired.contains(type), "Lifecycle event %s has already fired", type.name());
        handlers.computeIfAbsent(type, ignored -> new ArrayList<>()).add(new Registration<>(owner, handler));
    }

    @SuppressWarnings("unchecked")
    public synchronized <E extends CraftLifecycleEvent> void fire(final LifecycleEventType<? super E> type, final Function<Plugin, E> factory) throws IllegalStateException {
        Preconditions.checkState(fired.add(type), "Lifecycle event %s has already fired", type.name());
        final var registrations = handlers.remove(type);
        if (registrations == null) return;
        registrations.forEach(registration -> {
            final var event = factory.apply(registration.owner());
            try {
                ((LifecycleEventHandler<? super E>) registration.handler()).run(event);
            } catch (final RuntimeException e) {
                plugin.getComponentLogger().error("Plugin {} failed to handle lifecycle event {}",
                        registration.owner().getName(), type.name(), e);
            } finally {
                event.invalidate();
            }
        });
    }
}
