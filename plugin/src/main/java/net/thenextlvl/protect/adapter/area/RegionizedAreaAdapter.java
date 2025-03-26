package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.Region;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftRegionizedArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class RegionizedAreaAdapter<C extends Region, T extends CraftRegionizedArea<C>> implements AreaAdapter<T> {
    private final Key key;
    protected final ProtectPlugin plugin;

    public RegionizedAreaAdapter(ProtectPlugin plugin, @KeyPattern.Value String name) {
        this.key = Key.key("protect", name);
        this.plugin = plugin;
    }

    @Override
    public Key key() {
        return key;
    }
}
