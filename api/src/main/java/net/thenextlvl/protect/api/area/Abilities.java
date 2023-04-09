package net.thenextlvl.protect.api.area;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Abilities {

    /**
     * returns true if the player can place a block at a certain location
     *
     * @param player the player
     * @param location the location to check
     * @param material the type of the block to place
     * @return whether the player can interact at the specified location
     */
    boolean canPlace(Player player, Location location, Material material);

    /**
     * @see Abilities#canPlace(Player, Location, Material)
     */
    default boolean canPlace(Player player, Block block) {
        return canPlace(player, block.getLocation(), block.getType());
    }

    /**
     * returns true if the player can break a certain block at a certain location
     *
     * @param player the player
     * @param location the location of the block to check
     * @param material the type of the block to break
     * @return whether the player can break at the specified location
     */
    boolean canBreak(Player player, Location location, Material material);

    /**
     * @see Abilities#canBreak(Player, Location, Material)
     */
    default boolean canBreak(Player player, Block block) {
        return canBreak(player, block.getLocation(), block.getType());
    }

    /**
     * returns true if the player can interact at a certain location with a certain material
     *
     * @param player the player
     * @param location the location of the block that was interacted with
     * @param material the type of the block that was interacted with
     * @return whether the player can interact at the specified location
     */
    boolean canUse(Player player, Location location, Material material);

    /**
     * @see Abilities#canUse(Player, Location, Material)
     */
    default boolean canUse(Player player, Block block) {
        return canUse(player, block.getLocation(), block.getType());
    }
}
