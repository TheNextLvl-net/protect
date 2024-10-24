package net.thenextlvl.protect.area;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Getter
@FieldsAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public class CraftGlobalArea extends CraftArea implements GlobalArea {
    private final File dataFolder;
    private final File file;

    public CraftGlobalArea(ProtectPlugin plugin, World world, Map<Flag<?>, @Nullable Object> flags, int priority) {
        super(plugin, "@" + world.getName(), world, flags, priority);
        this.dataFolder = new File(world.getWorldFolder(), "areas");
        this.file = new File(getDataFolder(), getName() + ".json");
    }

    @Override
    public Optional<Area> getParent() {
        return Optional.empty();
    }

    @Override
    @NamePattern.Global
    @SuppressWarnings("PatternValidation")
    public String getName() {
        return super.getName();
    }

    @Override
    public boolean setParent(@Nullable Area parent) {
        return false;
    }

    @Override
    public boolean setParent(@Nullable String parent) {
        return false;
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
    public boolean equals(Object o) {
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
