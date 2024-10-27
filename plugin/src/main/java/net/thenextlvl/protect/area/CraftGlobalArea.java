package net.thenextlvl.protect.area;

import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Getter
public class CraftGlobalArea extends CraftArea implements GlobalArea {
    private final @NotNull File dataFolder;
    private final @NotNull File file;

    public CraftGlobalArea(
            @NotNull ProtectPlugin plugin,
            @NotNull World world,
            @NotNull Set<@NotNull UUID> members,
            @Nullable UUID owner,
            @NotNull Map<@NotNull Flag<?>, @Nullable Object> flags,
            int priority
    ) {
        super(plugin, "@" + world.getName(), world, members, owner, flags, priority);
        this.dataFolder = new File(world.getWorldFolder(), "areas");
        this.file = new File(getDataFolder(), getName() + ".json");
    }

    @Override
    public @NotNull Optional<Area> getParent() {
        return Optional.empty();
    }

    @Override
    public boolean canInteract(@NotNull Area area) {
        return equals(area);
    }

    @Override
    public @NotNull List<@NotNull Entity> getEntities() {
        return getWorld().getEntities();
    }

    @Override
    public @NotNull List<@NotNull Player> getPlayers() {
        return getWorld().getPlayers();
    }

    @Override
    public boolean contains(@NotNull Location location) {
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
