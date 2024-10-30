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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class GroupedRegion extends AbstractRegion {
    private final @NotNull Map<@NotNull String, @NotNull Region> regions = new HashMap<>();

    public GroupedRegion(@NotNull Map<@NotNull String, @NotNull Region> regions) {
        this(null, regions);
    }

    public GroupedRegion(@Nullable World world, @NotNull Map<@NotNull String, @NotNull Region> regions) {
        super(world);
        Preconditions.checkArgument(!regions.isEmpty(), "empty region map is not supported");
        this.regions.putAll(regions);
    }

    public @NotNull Map<@NotNull String, @NotNull Region> getRegions() {
        return Map.copyOf(regions);
    }

    public @Nullable Region getRegion(@NotNull String name) {
        return regions.get(name);
    }

    public boolean addRegion(@NotNull String name, @NotNull Region region) {
        return regions.putIfAbsent(name, region) == null;
    }

    public boolean hasRegion(@NotNull String name) {
        return regions.containsKey(name);
    }

    public boolean removeRegion(@NotNull String name) {
        return regions.remove(name) != null;
    }

    @Override
    public @NotNull BlockVector3 getMinimumPoint() {
        var regions = this.regions.values().toArray(new Region[0]);
        var maximum = regions[0].getMinimumPoint();
        for (var i = 1; i < regions.length; i++)
            maximum = regions[i].getMinimumPoint().getMinimum(maximum);
        return maximum;
    }

    @Override
    public @NotNull BlockVector3 getMaximumPoint() {
        var regions = this.regions.values().toArray(new Region[0]);
        var maximum = regions[0].getMaximumPoint();
        for (var i = 1; i < regions.length; i++)
            maximum = regions[i].getMaximumPoint().getMaximum(maximum);
        return maximum;
    }

    @Override
    public void expand(@NotNull BlockVector3... changes) throws RegionOperationException {
        throw new RegionOperationException(Caption.of("worldedit.selection.intersection.error.cannot-expand"));
    }

    @Override
    public void contract(@NotNull BlockVector3... changes) throws RegionOperationException {
        throw new RegionOperationException(Caption.of("worldedit.selection.intersection.error.cannot-contract"));
    }

    @Override
    public boolean contains(@NotNull BlockVector3 position) {
        return regions.values().stream().anyMatch(region -> region.contains(position));
    }

    @Override
    public @NotNull Iterator<@NotNull BlockVector3> iterator() {
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
    public @Nullable IChunkSet processSet(@NotNull IChunk chunk, @NotNull IChunkGet get, @NotNull IChunkSet set) {
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
    public @NotNull Future<?> postProcessSet(@NotNull IChunk chunk, @NotNull IChunkGet get, @NotNull IChunkSet set) {
        return new MultiFuture(regions.values().stream()
                .<Future<?>>map(region -> region.postProcessSet(chunk, get, set))
                .toList());
    }

    /**
     * @see com.sk89q.worldedit.regions.RegionIntersection#processSet(IChunk, IChunkGet, IChunkSet, boolean)
     * RegionIntersection#processSet(IChunk, IChunkGet, IChunkSet, boolean)
     */
    @Override
    public @Nullable IChunkSet processSet(@NotNull IChunk chunk, @NotNull IChunkGet get, @NotNull IChunkSet set, boolean asBlacklist) {
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
    public @NotNull Set<@NotNull BlockVector2> getChunks() {
        return regions.values().stream()
                .flatMap(region -> region.getChunks().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Set<@NotNull BlockVector3> getChunkCubes() {
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
}
