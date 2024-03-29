package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import core.file.FileIO;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class CraftAreaProvider implements AreaProvider {
    private final ProtectPlugin plugin;

    @Override
    public Stream<Area> getAreas() {
        return plugin.areasFiles().values().stream()
                .map(FileIO::getRoot)
                .flatMap(Collection::stream);
    }

    @Override
    public Stream<Area> getAreas(World world) {
        return getAreas().filter(area -> area.getWorld().equals(world));
    }

    @Override
    public Stream<Area> getAreas(Location location) {
        return getAreas(location.getWorld()).filter(area -> area.contains(location));
    }

    @Override
    public Stream<Area> getAreas(Block block) {
        return getAreas(block.getLocation());
    }

    @Override
    public Stream<Area> getAreas(Entity entity) {
        return getAreas(entity.getLocation());
    }

    @Override
    public GlobalArea getArea(World world) {
        return CraftGlobalArea.of(world);
    }

    @Override
    public Area getArea(Location location) {
        return getAreas(location)
                .max(Comparator.comparingInt(Area::getPriority))
                .orElseGet(() -> getArea(location.getWorld()));
    }

    @Override
    public Area getArea(Block block) {
        return getArea(block.getLocation());
    }

    @Override
    public Area getArea(Entity entity) {
        return getArea(entity.getLocation());
    }

    @Override
    public Optional<Area> getArea(String name) {
        return getAreas().filter(area -> area.getName().equals(name)).findAny();
    }
}
