package net.thenextlvl.protect.io;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import core.annotation.MethodsReturnNotNullByDefault;
import core.annotation.ParametersAreNotNullByDefault;
import core.annotation.TypesAreNotNullByDefault;
import net.thenextlvl.protect.area.Area;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

/**
 * The AreaAdapter interface provides methods to deserialize and serialize an object of a specific type T that
 * implements the Area interface. It is used to convert the object data between JsonObject and the implementation
 * class of the Area.
 *
 * @param <T> The type of the object that implements the Area interface.
 */
@TypesAreNotNullByDefault
@MethodsReturnNotNullByDefault
@ParametersAreNotNullByDefault
public interface AreaAdapter<T extends Area> {
    /**
     * Retrieves the key associated with the adapter. The key is used for serialization and deserialization
     * of objects of type T that implement the Area interface.
     *
     * @return The key associated with the adapter.
     */
    NamespacedKey getNamespacedKey();

    /**
     * Deserializes a JsonObject to an object of type T that implements the Area interface.
     *
     * @param object  The JsonObject to be deserialized.
     * @param world   The World associated with the Area.
     * @param context The JsonDeserializationContext used for deserialization.
     * @return The deserialized object of type T.
     */
    T deserialize(JsonObject object, World world, JsonDeserializationContext context);

    /**
     * Serializes the given area object to a JsonObject using the provided JsonSerializationContext.
     *
     * @param area    The area object to be serialized.
     * @param context The JsonSerializationContext used for serialization.
     * @return The serialized JsonObject representing the area object.
     */
    JsonObject serialize(T area, JsonSerializationContext context);
}
