package net.thenextlvl.protect.area;

import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NullMarked
public final class CraftGlobalArea extends CraftArea implements GlobalArea {
    private final File dataFolder = new File(getWorld().getWorldFolder(), "areas");
    private final File fallbackFile = new File(getDataFolder(), getName() + ".dat_old");
    private final File file = new File(getDataFolder(), getName() + ".dat");

    public CraftGlobalArea(ProtectPlugin plugin, World world) {
        super(plugin, "@" + world.getName(), world, Set.of(), null, Map.of(), -1);
    }

    public CraftGlobalArea(ProtectPlugin plugin, World world, CompoundTag tag) {
        super(plugin, world, "@" + world.getName(), tag);
    }

    @Override
    public Optional<Area> getParent() {
        return Optional.empty();
    }

    @Override
    public File getDataFolder() {
        return dataFolder;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public File getFallbackFile() {
        return fallbackFile;
    }

    @Override
    public boolean canInteract(Area area) {
        return equals(area);
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
    public boolean contains(Location location) {
        return getWorld().equals(location.getWorld());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CraftGlobalArea area = (CraftGlobalArea) o;
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
