package net.thenextlvl.protect.area.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents an abstract base class for events related to areas.
 *
 * @param <T> The type of the area of this event
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AreaEvent<T extends Area> extends Event {
    private static final @Getter HandlerList handlerList = new HandlerList();
    private final @NotNull T area;

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }
}
