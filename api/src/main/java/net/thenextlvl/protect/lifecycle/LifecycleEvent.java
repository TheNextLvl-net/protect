package net.thenextlvl.protect.lifecycle;

import net.thenextlvl.protect.lifecycle.event.FlagRegistrationEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

/**
 * An event passed to a {@link LifecycleEventHandler}.
 * Each handler receives its own event instance, bound to the plugin that registered the handler.
 * <p>
 * A lifecycle event is only valid while its handler runs; using it afterwards throws an {@link IllegalStateException}.
 *
 * @since 4.0.0
 */
public interface LifecycleEvent {
    /**
     * Fired once while Protect enables, before any area is loaded.
     * This is the only point at which flags can be registered.
     *
     * @since 4.0.0
     */
    LifecycleEventType<FlagRegistrationEvent> FLAG_REGISTRATION = new LifecycleEventType<>("flag_registration");

    /**
     * Retrieves the plugin that registered the handler receiving this event.
     *
     * @return the owning plugin
     * @since 4.0.0
     */
    @Contract(pure = true)
    Plugin owner();
}
