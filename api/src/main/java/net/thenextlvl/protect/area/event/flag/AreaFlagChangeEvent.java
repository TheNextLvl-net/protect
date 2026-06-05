package net.thenextlvl.protect.area.event.flag;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an event that occurs when a flag in an area is changed.
 * Cancelling this event results in the flag not being changed.
 *
 * @param <T> The type of the flag value.
 */
public final class AreaFlagChangeEvent<T> extends AreaFlagEvent {
    private static final HandlerList handlerList = new HandlerList();
    private @Nullable T newState;

    /**
     * Constructs a new AreaFlagChangeEvent, which represents the change of a flag within an area.
     *
     * @param area     The area associated with this event.
     * @param flag     The flag that is being changed.
     * @param newState The new state of the flag.
     */
    @ApiStatus.Internal
    public AreaFlagChangeEvent(final Area area, final Flag flag, @Nullable final T newState) {
        super(area, flag);
        this.newState = newState;
    }

    @Contract(pure = true)
    public Optional<T> getNewState() {
        return Optional.ofNullable(newState);
    }

    @Contract(mutates = "this")
    public void setNewState(final T newState) {
        this.newState = newState;
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
