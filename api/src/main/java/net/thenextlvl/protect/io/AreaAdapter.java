package net.thenextlvl.protect.io;

import core.nbt.tag.CompoundTag;
import net.kyori.adventure.key.Keyed;
import net.thenextlvl.protect.area.Area;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

/**
 * The AreaAdapter interface provides methods to deserialize and serialize an object of a specific type T that
 * implements the Area interface. It is used to convert the object data between JsonObject and the implementation
 * class of the Area.
 *
 * @param <T> The type of the object that implements the Area interface.
 */
@NullMarked
public interface AreaAdapter<T extends Area> extends Keyed {
    /**
     * Retrieves the key associated with the adapter.
     * The key is used for de/serialization of objects that implement the Area interface.
     *
     * @return The key associated with the adapter.
     */
    NamespacedKey key();

    Area construct(World world, String name, CompoundTag tag);
}
