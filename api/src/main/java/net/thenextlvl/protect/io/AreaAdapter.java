package net.thenextlvl.protect.io;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.thenextlvl.nbt.tag.CompoundTag;
import net.thenextlvl.protect.area.Area;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

/**
 * Interface for adapting Area objects for serialization and deserialization.
 *
 * @param <T> the type of Area that this adapter handles
 */
@NullMarked
public interface AreaAdapter<T extends Area> extends Keyed {
    /**
     * Retrieves the key associated with the adapter.
     * The key is used for de/serialization of objects that implement the Area interface.
     *
     * @return The key associated with the adapter.
     */
    Key key();

    /**
     * Constructs an Area object using the provided world, name, and CompoundTag.
     *
     * @param world the World associated with the Area
     * @param name  the name of the Area
     * @param tag   the CompoundTag containing the serialized data of the Area
     * @return the constructed Area object
     */
    Area construct(World world, String name, CompoundTag tag);
}
