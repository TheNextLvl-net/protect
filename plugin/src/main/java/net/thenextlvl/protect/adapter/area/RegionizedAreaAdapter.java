package net.thenextlvl.protect.adapter.area;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.AreaCreator;
import net.thenextlvl.protect.area.CraftRegionizedArea;
import net.thenextlvl.protect.io.AreaAdapter;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Accessors(fluent = true)
public abstract class RegionizedAreaAdapter<C extends Region, T extends CraftRegionizedArea<C>> implements AreaAdapter<T> {
    @Getter
    private final NamespacedKey key;
    protected final ProtectPlugin plugin;

    public RegionizedAreaAdapter(ProtectPlugin plugin, @KeyPattern.Value String name) {
        this.key = new NamespacedKey(plugin, name);
        this.plugin = plugin;
    }

    protected abstract T construct(AreaCreator<C> creator);
}
