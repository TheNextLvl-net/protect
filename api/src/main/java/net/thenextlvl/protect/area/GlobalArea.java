package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The GlobalArea interface represents the area of an entire world.
 */
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface GlobalArea extends Area {

    @Override
    default List<Entity> getEntities() {
        return getWorld().getEntities();
    }

    @Override
    default List<Player> getPlayers() {
        return getWorld().getPlayers();
    }

    @Override
    default boolean contains(Location location) {
        return getWorld().equals(location.getWorld());
    }
}
