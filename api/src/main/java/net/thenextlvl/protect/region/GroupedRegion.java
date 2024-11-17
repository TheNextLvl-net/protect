package net.thenextlvl.protect.region;

import com.fastasyncworldedit.core.configuration.Caption;
import com.fastasyncworldedit.core.queue.IChunk;
import com.fastasyncworldedit.core.queue.IChunkGet;
import com.fastasyncworldedit.core.queue.IChunkSet;
import com.fastasyncworldedit.core.util.MultiFuture;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Represents a region composed of multiple named subregions.
 * <p>
 * The GroupedRegion class allows for managing a collection of regions identified by unique names.
 */
@NullMarked
public class GroupedRegion extends AbstractRegion {
    private final Map<String, Region> regions = new HashMap<>();

    /**
     * Constructs a GroupedRegion with the specified regions.
     *
     * @param regions A map of regions where the key is the region name and the value is the Region instance.
     *                The map mustn't be empty.
     */
    public GroupedRegion(Map<String, Region> regions) {
        this(null, regions);
    }

    /**
     * Constructs a GroupedRegion with the specified world and regions.
     *
     * @param world   The world associated with this GroupedRegion. Can be null.
     * @param regions A map of regions where the key is the region name and the value is the Region instance.
     *                The map mustn't be empty.
     */
    public GroupedRegion(@Nullable World world, Map<String, Region> regions) {
        super(world);
        Preconditions.checkArgument(!regions.isEmpty(), "empty region map is not supported");
        this.regions.putAll(regions);
    }

    /**
     * Retrieves an unmodifiable view of the region map contained within this GroupedRegion.
     *
     * @return an unmodifiable map of regions where the key is the region name and the value is the Region instance.
     */
    public Map<String, Region> getRegions() {
        return Map.copyOf(regions);
    }

    /**
     * Retrieves a region by its name.
     *
     * @param name The name of the region to retrieve.
     * @return The region associated with the specified name, or null if no such region exists.
     */
    public @Nullable Region getRegion(String name) {
        return regions.get(name);
    }

    /**
     * Adds a region to the grouped regions map if it is not already present.
     *
     * @param name   The name of the region to add.
     * @param region The region instance to be added.
     * @return true if the region was added successfully, false if a region with the same name already exists.
     */
    public boolean addRegion(String name, Region region) {
        return regions.putIfAbsent(name, region.clone()) == null;
    }

    /**
     * Checks if a region with the specified name exists in the grouped regions map.
     *
     * @param name The name of the region to check for existence.
     * @return true if a region with the specified name exists, false otherwise.
     */
    public boolean hasRegion(String name) {
        return regions.containsKey(name);
    }

    /**
     * Checks if the specified region exists in the grouped regions map.
     *
     * @param region The region to check for existence.
     * @return true if the region exists in the map, false otherwise.
     */
    public boolean hasRegion(Region region) {
        return regions.containsValue(region);
    }

    /**
     * Removes a region by its name from the grouped regions map if the map contains more than one region.
     *
     * @param name The name of the region to remove.
     * @return true if the region was successfully removed, false otherwise.
     */
    public boolean removeRegion(String name) {
        return regions.size() > 1 && regions.remove(name) != null;
    }

    /**
     * Sets or updates the specified region in the grouped regions map.
     *
     * @param name   The name of the region to set or update.
     * @param region The Region instance to be associated with the specified name.
     */
    public void setRegion(String name, Region region) {
        regions.put(name, region.clone());
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        var regions = this.regions.values().toArray(new Region[0]);
        var maximum = regions[0].getMinimumPoint();
        for (var i = 1; i < regions.length; i++)
            maximum = regions[i].getMinimumPoint().getMinimum(maximum);
        return maximum;
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        var regions = this.regions.values().toArray(new Region[0]);
        var maximum = regions[0].getMaximumPoint();
        for (var i = 1; i < regions.length; i++)
            maximum = regions[i].getMaximumPoint().getMaximum(maximum);
        return maximum;
    }

    @Override
    public void expand(BlockVector3... changes) throws RegionOperationException {
        throw new RegionOperationException(Caption.of("worldedit.selection.intersection.error.cannot-expand"));
    }

    @Override
    public void contract(BlockVector3... changes) throws RegionOperationException {
        throw new RegionOperationException(Caption.of("worldedit.selection.intersection.error.cannot-contract"));
    }

    @Override
    public boolean contains(BlockVector3 position) {
        return regions.values().stream().anyMatch(region -> region.contains(position));
    }

    @Override
    public Iterator<BlockVector3> iterator() {
        return Iterators.concat(Iterators.transform(regions.values().iterator(), Iterable::iterator));

    }

    @Override
    public boolean containsEntireCuboid(int bx, int tx, int by, int ty, int bz, int tz) {
        return regions.values().stream().anyMatch(region -> region.containsEntireCuboid(bx, tx, by, ty, bz, tz));
    }

    /**
     * @see com.sk89q.worldedit.regions.RegionIntersection#processSet(IChunk, IChunkGet, IChunkSet)
     * RegionIntersection#processSet(IChunk, IChunkGet, IChunkSet)
     */
    @Override
    public @Nullable IChunkSet processSet(IChunk chunk, IChunkGet get, IChunkSet set) {
        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;
        int tx = bx + 15;
        int tz = bz + 15;
        List<Region> intersecting = new ArrayList<>(2);
        for (Region region : regions.values()) {
            BlockVector3 regMin = region.getMinimumPoint();
            BlockVector3 regMax = region.getMaximumPoint();
            if (tx >= regMin.x() && bx <= regMax.x() && tz >= regMin.z() && bz <= regMax.z()) {
                intersecting.add(region);
            }
        }
        if (intersecting.isEmpty()) {
            return null;
        }
        if (intersecting.size() == 1) {
            return intersecting.getFirst().processSet(chunk, get, set);
        }
        return super.processSet(chunk, get, set);
    }

    @Override
    public Future<?> postProcessSet(IChunk chunk, IChunkGet get, IChunkSet set) {
        return new MultiFuture(regions.values().stream()
                .<Future<?>>map(region -> region.postProcessSet(chunk, get, set))
                .toList());
    }

    /**
     * @see com.sk89q.worldedit.regions.RegionIntersection#processSet(IChunk, IChunkGet, IChunkSet, boolean)
     * RegionIntersection#processSet(IChunk, IChunkGet, IChunkSet, boolean)
     */
    @Override
    public @Nullable IChunkSet processSet(IChunk chunk, IChunkGet get, IChunkSet set, boolean asBlacklist) {
        if (!asBlacklist) {
            return processSet(chunk, get, set);
        }
        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;
        int tx = bx + 15;
        int tz = bz + 15;
        for (Region region : regions.values()) {
            BlockVector3 regMin = region.getMinimumPoint();
            BlockVector3 regMax = region.getMaximumPoint();
            if (tx >= regMin.x() && bx <= regMax.x() && tz >= regMin.z() && bz <= regMax.z()) {
                set = region.processSet(chunk, get, set, true);
            }
        }
        return set;
    }

    @Override
    public Set<BlockVector2> getChunks() {
        return regions.values().stream()
                .flatMap(region -> region.getChunks().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<BlockVector3> getChunkCubes() {
        return regions.values().stream()
                .flatMap(region -> region.getChunkCubes().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean containsChunk(int chunkX, int chunkZ) {
        return regions.values().stream().anyMatch(region -> region.containsChunk(chunkX, chunkZ));
    }

    @Override
    public boolean contains(int x, int z) {
        return regions.values().stream().anyMatch(region -> region.contains(x, z));
    }

    @Override
    public boolean contains(int x, int y, int z) {
        return regions.values().stream().anyMatch(region -> region.contains(x, y, z));
    }

    @Override
    public GroupedRegion clone() {
        var clone = (GroupedRegion) super.clone();
        clone.regions.putAll(regions);
        return clone;
    }
}
