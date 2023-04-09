package net.thenextlvl.protect.api.area;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public interface Container {

    /**
     * Returns true if the location is contained
     *
     * @param location the location
     * @return true if contained
     */
    boolean contains(Location location);

    /**
     * @see Container#contains(Location)
     */
    default boolean contains(Block block) {
        return contains(block.getLocation());
    }

    /**
     * @see Container#contains(Location)
     */
    default boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }

    /**
     * returns true if the container is too big
     *
     * @return whether the container is too big
     */
    boolean isTooBig();
}
