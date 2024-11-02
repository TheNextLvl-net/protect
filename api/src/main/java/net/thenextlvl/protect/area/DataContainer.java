package net.thenextlvl.protect.area;

import core.nbt.tag.Tag;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * A data container stores key-value pairs, where keys are of type {@code Key} and values are of type {@code Tag}.
 */
@NullMarked
public interface DataContainer {
    /**
     * Retrieves the value associated with the specified key if present.
     *
     * @param <T> the type of the value extending from Tag
     * @param key the key whose associated value is to be returned
     * @return an {@code Optional} containing the value if present, or an empty {@code Optional} if not
     */
    <T extends Tag> Optional<T> get(Key key);

    /**
     * Removes the entry for the specified key if it is present.
     *
     * @param <T> the type of the value extending from Tag
     * @param key the key whose associated value is to be removed
     * @return an {@link Optional} containing the removed value, or {@link Optional#empty()}
     */
    <T extends Tag> Optional<T> remove(Key key);

    /**
     * Retrieves the value associated with the specified key if present, otherwise returns the provided default value.
     *
     * @param <T>          the type of the value extending from Tag
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default value to return if the key is not present
     * @return the value associated with the specified key, or the default value if the key is not present
     */
    <T extends Tag> T getOrDefault(Key key, T defaultValue);

    /**
     * Sets the specified value for the given key in the data container.
     *
     * @param <T>   the type of the value extending from Tag
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    <T extends Tag> void set(Key key, T value);

    /**
     * Retrieves a map of all entries stored in the data container.
     * The keys are of type {@code Key} and the values are of type {@code Tag}.
     *
     * @return an unmodifiable map containing all entries.
     */
    Map<Key, Tag> getTags();

    /**
     * Retrieves a set of all keys present in the data container.
     *
     * @return an unmodifiable set of keys contained in the data container
     */
    Set<Key> getKeys();

    /**
     * Returns a set view of the entries contained in this data container.
     *
     * @return an unmodifiable set of all entries
     */
    Set<Map.Entry<Key, Tag>> entrySet();

    /**
     * Checks if the specified key is present in the data container.
     *
     * @param key the key to check for presence
     * @return {@code true} if the key is present, {@code false} otherwise
     */
    boolean has(Key key);

    /**
     * Checks if the data container is empty.
     *
     * @return {@code true} if the container is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Removes all entries from the data container, erasing all stored data.
     */
    void clear();

    /**
     * Copies all entries from the current data container to the specified data container.
     *
     * @param dataContainer the target data container to which the entries are copied
     */
    void copyTo(DataContainer dataContainer);

    /**
     * Copies all entries from the current data container to the specified data container.
     *
     * @param dataContainer the target data container to which the entries are copied
     * @param replace       whether the target data container will be cleared before copying the entries
     */
    void copyTo(DataContainer dataContainer, boolean replace);

    /**
     * Performs the given action for each entry in the data container.
     *
     * @param action the action to be performed for each entry
     */
    void forEach(BiConsumer<Key, Tag> action);
}
