package net.thenextlvl.protect.adapter.area;

import core.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Accessors(fluent = true)
public class GlobalAreaAdapter implements AreaAdapter<CraftGlobalArea> {
    private final @Getter NamespacedKey key;
    private final ProtectPlugin plugin;

    public GlobalAreaAdapter(ProtectPlugin plugin) {
        this.key = new NamespacedKey(plugin, "global");
        this.plugin = plugin;
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftGlobalArea(plugin, world);
    }
}
