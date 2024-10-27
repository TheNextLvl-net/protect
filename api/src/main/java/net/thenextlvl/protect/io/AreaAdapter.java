package net.thenextlvl.protect.io;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.kyori.adventure.key.Keyed;
import net.thenextlvl.protect.area.Area;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * The AreaAdapter interface provides methods to deserialize and serialize an object of a specific type T that
 * implements the Area interface. It is used to convert the object data between JsonObject and the implementation
 * class of the Area.
 *
 * @param <T> The type of the object that implements the Area interface.
 */
public interface AreaAdapter<T extends Area> extends Keyed {
    /**
     * Retrieves the key associated with the adapter.
     * The key is used for de/serialization of objects that implement the Area interface.
     *
     * @return The key associated with the adapter.
     */
    @NotNull NamespacedKey key();

    /**
     * Deserializes a JsonObject to an object of type T that implements the Area interface.
     *
     * @param object  The JsonObject to be deserialized.
     * @param world   The World associated with the Area.
     * @param context The JsonDeserializationContext used for deserialization.
     * @return The deserialized object of type T.
     */
    @NotNull T deserialize(@NotNull JsonObject object, @NotNull World world, @NotNull JsonDeserializationContext context);

    /**
     * Serializes the given area object to a JsonObject using the provided JsonSerializationContext.
     *
     * @param area    The area object to be serialized.
     * @param context The JsonSerializationContext used for serialization.
     * @return The serialized JsonObject representing the area object.
     */
    @NotNull JsonObject serialize(@NotNull T area, @NotNull JsonSerializationContext context);
}
