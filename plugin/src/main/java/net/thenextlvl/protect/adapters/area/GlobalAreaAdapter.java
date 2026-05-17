package net.thenextlvl.protect.adapters.area;

import net.kyori.adventure.key.Key;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CraftGlobalArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GlobalAreaAdapter implements AreaAdapter<CraftGlobalArea> {
    private final Key key;
    private final ProtectPlugin plugin;

    public GlobalAreaAdapter(final ProtectPlugin plugin) {
        this.key = Key.key("protect:global");
        this.plugin = plugin;
    }

    @Override
    public Area construct(final World world, final String name, final CompoundTag tag) {
        return new CraftGlobalArea(plugin, world, tag);
    }

    @Override
    public Key key() {
        return key;
    }
}
