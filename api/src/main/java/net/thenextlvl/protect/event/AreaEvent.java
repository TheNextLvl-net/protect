package net.thenextlvl.protect.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.area.Area;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AreaEvent extends Event {
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final Area area;

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }
}
