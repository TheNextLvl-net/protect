package net.thenextlvl.protect.lifecycle.event;

import net.thenextlvl.protect.flag.FlagInstance;
import net.thenextlvl.protect.flag.FlagRegistry;
import net.thenextlvl.protect.lifecycle.LifecycleEvent;
import org.jetbrains.annotations.Contract;

/**
 * Lets a plugin register its flags.
 * Once all handlers have run, the {@link FlagRegistry} is frozen for the rest of the server's lifetime.
 *
 * @see net.thenextlvl.protect.lifecycle.LifecycleEvent#FLAG_REGISTRATION
 * @since 4.0.0
 */
public interface FlagRegistrationEvent extends LifecycleEvent {
    /**
     * Retrieves a read-only view of the flags registered so far, including Protect's own.
     *
     * @return the flag registry
     * @since 4.0.0
     */
    @Contract(pure = true)
    FlagRegistry registry();

    /**
     * Registers the given flag on behalf of {@link #owner()}.
     *
     * @param flag the flag to register
     * @param <F>  the type of the flag
     * @return the registered flag
     * @throws IllegalStateException if a flag with the same key is already registered
     * @since 4.0.0
     */
    @Contract(value = "_ -> param1", mutates = "this")
    <F extends FlagInstance<?>> F register(F flag) throws IllegalStateException;
}
