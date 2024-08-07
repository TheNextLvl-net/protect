package net.thenextlvl.protect.area;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.Map;

/**
 * The AreaService interface is used to create or delete instances of {@link Area}.
 */
@TypesAreNotNullByDefault
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
    CuboidArea create(@NamePattern.Regionized String name, World world, BlockVector3 pos1, BlockVector3 pos2);

    /**
     * Creates a CuboidArea object with the given name and region.
     *
     * @param name   The name of the CuboidArea.
     * @param world  The world in which the CuboidArea is located
     * @param region The region of the CuboidArea.
     * @return The created CuboidArea object.
     */
    CuboidArea create(@NamePattern.Regionized String name, World world, CuboidRegion region);

    /**
     * Deletes the given Area.
     *
     * @param area The Area to delete.
     * @param <T>  The type of the region.
     * @return True if the deletion is successful, false otherwise.
     */
    <T extends Region> boolean delete(RegionizedArea<T> area);

    /**
     * Retrieves all registered adapter functions for serializing and deserializing Area objects.
     *
     * @return The registered adapter functions for serializing and deserializing Area objects.
     */
    Map<Class<? extends Area>, AreaAdapter<?>> getAdapters();

    /**
     * Registers an adapter for serializing and deserializing Area objects of the specified type.
     *
     * @param type    The class object representing the type of the area object.
     * @param adapter The adapter for the specified area type.
     * @param <T>     The type parameter representing the area object.
     * @throws IllegalStateException if an adapter for the specified area type is already registered.
     */
    <T extends Area> void registerAdapter(Class<T> type, AreaAdapter<T> adapter) throws IllegalStateException;

    /**
     * Unregisters an adapter for serializing and deserializing Area objects of the specified type.
     *
     * @param type The class object representing the type of the area object.
     * @param <T>  The type parameter representing the area object.
     * @return true if the unregistration is successful, false otherwise.
     */
    <T extends Area> boolean unregisterAdapter(Class<T> type);

    /**
     * Retrieves the adapter for serializing and deserializing objects of type T that implement the Area interface.
     *
     * @param type The class object representing the type of the area object.
     * @param <T>  The type of the area object.
     * @return The adapter for the specified area type.
     * @throws NullPointerException if no adapter was registered for the given type.
     */
    <T extends Area> AreaAdapter<T> getAdapter(Class<T> type) throws NullPointerException;

    /**
     * Retrieves the adapter for serializing and deserializing objects of type T that implement the Area interface.
     *
     * @param key The NamespacedKey representing the type of the area object.
     * @param <T> The type of the area object.
     * @return The adapter for the specified area type.
     * @throws IllegalArgumentException thrown if no adapter for the specified key was registered.
     */
    <T extends Area> AreaAdapter<T> getAdapter(NamespacedKey key) throws IllegalArgumentException;
}
