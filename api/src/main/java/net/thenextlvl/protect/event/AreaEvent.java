package net.thenextlvl.protect.event;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This class represents an abstract base class for events related to areas.
 *
 * @param <T> The type of the area of this event
 */
@Getter
@TypesAreNotNullByDefault
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AreaEvent<T extends Area> extends Event {
    private static final @Getter HandlerList handlerList = new HandlerList();
    private final T area;

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
