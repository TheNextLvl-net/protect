package net.thenextlvl.protect.area;

import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NullMarked
public final class CraftGlobalArea extends CraftArea implements GlobalArea {
    private final Path dataFolder = ProtectPlugin.getDataFolder(getWorld());
    private final Path fallbackFile = dataFolder.resolve(getName() + ".dat_old");
    private final Path file = dataFolder.resolve(getName() + ".dat");

    public CraftGlobalArea(final ProtectPlugin plugin, final World world) {
        super(plugin, "@" + world.getName(), world, Set.of(), Set.of(), null, Map.of(), -1);
    }

    public CraftGlobalArea(final ProtectPlugin plugin, final World world, final CompoundTag tag) {
        super(plugin, world, "@" + world.getName(), tag);
    }

    @Override
    public Optional<Area> getParent() {
        return Optional.empty();
    }

    @Override
    public Path getDataPath() {
        return dataFolder;
    }

    @Override
    public Path getDataFile() {
        return file;
    }

    @Override
    public Path getBackupFile() {
        return fallbackFile;
    }

    @Override
    public boolean canInteract(final Area area) {
        return equals(area) || area.getParent().map(this::equals).orElse(false);
    }

    @Override
    public List<Entity> getEntities() {
        return getWorld().getEntities();
    }

    @Override
    public List<Player> getPlayers() {
        return getWorld().getPlayers();
    }

    @Override
    public List<Entity> getHighestEntities() {
        return getEntities();
    }

    @Override
    public List<Player> getHighestPlayers() {
        return getPlayers();
    }

    @Override
    public boolean contains(final Location location) {
        return getWorld().equals(location.getWorld());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final CraftGlobalArea area = (CraftGlobalArea) o;
        return Objects.equals(getWorld(), area.getWorld());
    }

    @Override
    public int hashCode() {
        return getWorld().hashCode();
    }

    @Override
    public String toString() {
        return "CraftGlobalArea{world=" + getWorld() + "}";
    }
}
