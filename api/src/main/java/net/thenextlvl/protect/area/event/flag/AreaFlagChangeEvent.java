package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.FlagInstance;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents an event that occurs when a flag in an area is changed.
 * Cancelling this event results in the flag not being changed.
 */
public final class AreaFlagChangeEvent extends AreaFlagEvent {
    private static final HandlerList handlerList = new HandlerList();
    private Flag<?> flag;

    /**
     * Constructs a new AreaFlagChangeEvent, which represents the change of a flag within an area.
     *
     * @param area The area associated with this event.
     * @param flag The flag that is being changed.
     */
    @ApiStatus.Internal
    public AreaFlagChangeEvent(final Area area, final Flag<?> flag) {
        super(area, flag.instance());
        this.flag = flag;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T> Flag<T> getFlag() {
        return (Flag<T>) flag;
    }

    /**
     * Sets the new state to apply to the flag when this event is not canceled.
     * The state must be an instance of the type declared by the flag's {@link FlagInstance#flagType()}.
     *
     * @param newState The new state of the flag.
     * @throws ClassCastException if the state is not an instance of the flag's declared type.
     * @since 4.0.0
     */
    @Contract(mutates = "this")
    @SuppressWarnings("unchecked")
    public void setFlag(final Object newState) {
        final var instance = (FlagInstance<Object>) getFlagInstance();
        this.flag = instance.withValue(instance.flagType().type().cast(newState));
    }

    @Override
    @Contract(pure = true)
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Contract(pure = true)
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
