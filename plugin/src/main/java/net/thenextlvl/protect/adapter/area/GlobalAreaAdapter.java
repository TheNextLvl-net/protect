package net.thenextlvl.protect.adapter.area;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GlobalAreaAdapter implements AreaAdapter<CraftGlobalArea> {
    private final Key key;
    private final ProtectPlugin plugin;

    public GlobalAreaAdapter(ProtectPlugin plugin) {
        this.key = Key.key("protect:global");
        this.plugin = plugin;
    }

    @Override
    public Area construct(World world, String name, CompoundTag tag) {
        return new CraftGlobalArea(plugin, world, tag);
    }

    @Override
    public Key key() {
        return key;
    }
}
