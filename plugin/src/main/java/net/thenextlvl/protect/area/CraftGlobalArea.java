package net.thenextlvl.protect.area;

import core.annotation.FieldsAreNotNullByDefault;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

@Getter
@FieldsAreNotNullByDefault
@ToString(callSuper = true)
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
@EqualsAndHashCode(callSuper = true)
public class CraftGlobalArea extends CraftArea implements GlobalArea {
    private final File dataFolder;
    private final File file;

    @SuppressWarnings("PatternValidation")
    public CraftGlobalArea(World world, int priority) {
        super("@" + world.getName(), world, priority);
        this.dataFolder = new File(world.getWorldFolder(), "areas");
        this.file = new File(getDataFolder(), getName() + ".json");
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
}
