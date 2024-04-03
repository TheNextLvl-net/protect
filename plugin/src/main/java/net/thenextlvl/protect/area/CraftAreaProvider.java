package net.thenextlvl.protect.area;

import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import core.file.FileIO;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.World;

import java.util.Collection;
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
    public GlobalArea getArea(World world) {
        return CraftGlobalArea.of(world);
    }
}
