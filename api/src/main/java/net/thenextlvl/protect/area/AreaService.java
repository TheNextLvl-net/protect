package net.thenextlvl.protect.area;

import com.sk89q.worldedit.regions.Region;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * The AreaService interface is used to create or delete instances of {@link Area}.
 */
@NullMarked
public interface AreaService {
    /**
     * Creates an AreaCreator with the given name, world, and region.
     *
     * @param name   The name of the area.
     * @param world  The world in which the area is located.
     * @param region The region of the area.
     * @param <T>    The type of the region.
     * @return The AreaCreator object.
     */
    <T extends Region> AreaCreator<T> creator(
            String name,
            World world,
            T region
    );

    /**
     * Creates an AreaCreator for the given RegionizedArea.
     *
     * @param area The RegionizedArea for which the AreaCreator will be created.
     * @return The AreaCreator object for the specified RegionizedArea.
     */
    <T extends Region> AreaCreator<T> creator(RegionizedArea<T> area);

    /**
     * Deletes the given Area.
     *
     * @param area The Area to delete.
     * @param <T>  The type of the region.
     * @return True, if the deletion is successful, false otherwise.
     */
    <T extends Region> boolean delete(RegionizedArea<T> area);

    /**
     * Retrieves an Optional containing the RegionWrapper of the specified type if it exists.
     *
     * @param <T>  The type of the region.
     * @param type The class object representing the type of the region.
     * @return An Optional containing the RegionWrapper of the specified type if it exists, otherwise an empty Optional.
     */
    <T extends Region> Optional<Function<AreaCreator<T>, RegionizedArea<T>>> getWrapper(Class<T> type);

    /**
     * Unregisters the region wrapper of the specified type.
     *
     * @param <T>  The type of the region.
     * @param type The class object representing the type of the region.
     * @return true if the unregistration is successful, false otherwise.
     */
    <T extends Region> boolean unregisterWrapper(Class<T> type);

    /**
     * Registers a new region wrapper using the specified type and creator function.
     *
     * @param <T>     The type of the region.
     * @param type    The class object representing the type of the region.
     * @param creator The function to create a RegionizedArea from an AreaCreator.
     * @throws IllegalStateException if a region wrapper for the same type already exists.
     */
    <T extends Region> void registerWrapper(
            Class<T> type,
            Function<AreaCreator<T>, RegionizedArea<T>> creator
    ) throws IllegalStateException;

    /**
     * Retrieves an unmodifiable set of region wrapper classes.
     *
     * @return an unmodifiable set of classes representing region wrappers.
     */
    Set<Class<? extends Region>> getRegionWrappers();

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
    <T extends Area> void registerAdapter(
            Class<T> type,
            AreaAdapter<T> adapter
    ) throws IllegalStateException;

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
     * @param key The Key representing the type of the area object.
     * @param <T> The type of the area object.
     * @return The adapter for the specified area type.
     * @throws IllegalArgumentException thrown if no adapter for the specified key was registered.
     */
    <T extends Area> AreaAdapter<T> getAdapter(Key key) throws IllegalArgumentException;
}
