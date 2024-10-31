package net.thenextlvl.protect.adapter.area;

import core.file.FileIO;
import core.io.IO;
import core.nbt.file.NBTFile;
import core.nbt.tag.CompoundTag;
import lombok.Getter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.attribute.FileAttribute;

@Getter
@NullMarked
public class AreaFile extends NBTFile<CompoundTag> {
    private final ProtectPlugin plugin;
    private final Area area;
    private final World world;

    public AreaFile(ProtectPlugin plugin, IO io, World world, @Nullable Area area) {
        super(io, new CompoundTag());
        this.plugin = plugin;
        this.world = world;
        this.area = area != null ? read(area) : readFull();
    }

    public AreaFile(ProtectPlugin plugin, Area area) {
        this(plugin, IO.of(area.getFile()), area.getWorld(), area);
    }

    private Area read(Area area) {
        var root = getRoot();
        area.deserialize(root);
        return area;
    }

    private Area readFull() {
        var root = getRoot();
        var type = readType(root);
        return read(plugin.areaService().getAdapter(type)
                .construct(world, getRootName(), root));
    }

    @Override
    public FileIO<CompoundTag> save(FileAttribute<?>... attributes) {
        setRootName(area.getName());
        setRoot(area.serialize().getAsCompound());
        return super.save(attributes);
    }

    private NamespacedKey readType(CompoundTag root) {
        var split = root.get("type").getAsString().split(":", 2);
        return new NamespacedKey(split[0], split[1]);
    }
}
