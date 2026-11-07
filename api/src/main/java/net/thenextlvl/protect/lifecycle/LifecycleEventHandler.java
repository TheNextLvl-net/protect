package net.thenextlvl.protect.lifecycle;

/**
 * Handles a lifecycle event.
 *
 * @param <E> the type of the event
 * @since 4.0.0
 */
@FunctionalInterface
public interface LifecycleEventHandler<E extends LifecycleEvent> {
    /**
     * Called when the event fires.
     *
     * @param event the event
     * @since 4.0.0
     */
    void run(E event);
}
