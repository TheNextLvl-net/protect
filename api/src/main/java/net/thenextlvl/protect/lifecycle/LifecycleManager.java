package net.thenextlvl.protect.lifecycle;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.ServiceLoader;

/**
 * Manages the lifecycle of Protect and the handlers that hook into it.
 * <p>
 * Handlers must be registered before their event type fires,
 * which in practice means during {@link Plugin#onLoad()} of a plugin that depends on Protect.
 * All plugins are loaded before Protect enables, and Protect fires its lifecycle events while enabling,
 * before any area is loaded.
 * <pre>{@code
 * private final FlagInstance<Boolean> enter = FlagInstance.create(Key.key("plugin", "enter"), BooleanFlagType.booleanType(), true);
 *
 * @Override
 * public void onLoad() {
 *     LifecycleManager.instance().registerHandler(this, LifecycleEvents.FLAG_REGISTRATION, event -> event.register(enter));
 * }
 * }</pre>
 *
 * @since 4.0.0
 */
public interface LifecycleManager {
    static LifecycleManager instance() {
        final class Holder {
            private static final LifecycleManager INSTANCE = ServiceLoader.load(LifecycleManager.class).findFirst().orElseThrow();
        }
        return Holder.INSTANCE;
    }
    
    /**
     * Registers a handler that is called once when the given event type fires.
     *
     * @param owner   the plugin owning the handler
     * @param type    the event type to handle
     * @param handler the handler to call
     * @param <E>     the type of the event
     * @throws IllegalStateException if the event type has already fired
     * @since 4.0.0
     */
    @Contract(mutates = "this")
    <E extends LifecycleEvent> void registerHandler(Plugin owner, LifecycleEventType<E> type, LifecycleEventHandler<? super E> handler) throws IllegalStateException;
}
