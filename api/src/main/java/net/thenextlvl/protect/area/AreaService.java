package net.thenextlvl.protect.area;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import org.bukkit.World;

/**
 * The AreaService interface is used to create or delete instances of {@link Area}.
 */
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface AreaService {
    /**
     * Creates a CuboidArea object with the given parameters.
     *
     * @param name  The name of the CuboidArea.
     * @param world The world in which the CuboidArea is located.
     * @param pos1  The first corner of the CuboidArea.
     * @param pos2  The second corner of the CuboidArea.
     * @return The created CuboidArea object.
     */
    CuboidArea create(@NamePattern String name, World world, BlockVector3 pos1, BlockVector3 pos2);

    /**
     * Creates a CuboidArea object with the given name and region.
     *
     * @param name   The name of the CuboidArea.
     * @param region The region of the CuboidArea.
     * @return The created CuboidArea object.
     */
    CuboidArea create(@NamePattern String name, CuboidRegion region);

    /**
     * Deletes the given Area.
     *
     * @param area The Area to delete.
     * @param <T> The type of the region.
     * @return True if the deletion is successful, false otherwise.
     */
    <T extends Region> boolean delete(RegionizedArea<T> area);
}
