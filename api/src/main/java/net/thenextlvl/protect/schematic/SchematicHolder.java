package net.thenextlvl.protect.schematic;

import com.sk89q.worldedit.WorldEditException;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;

/**
 * The SchematicHolder interface represents an object that holds and manages a schematic file.
 */
@NullMarked
public interface SchematicHolder {
    /**
     * Retrieves the schematic file associated with this object.
     *
     * @return The schematic file.
     */
    @Contract(pure = true)
    File getSchematic();

    /**
     * Deletes the schematic file associated with this object.
     * Returns true if the schematic file was successfully deleted, false otherwise.
     *
     * @return True if the schematic file was successfully deleted, false otherwise.
     */
    @Contract(mutates = "io")
    boolean deleteSchematic();

    /**
     * Saves the schematic associated with this object.
     *
     * @throws IOException        if an I/O error occurs while saving the schematic file.
     * @throws WorldEditException if an error occurs while using WorldEdit API to save the schematic.
     */
    @Contract(mutates = "io")
    void saveSchematic() throws IOException, WorldEditException;

    /**
     * Loads the schematic associated with this object.
     *
     * @return true if the schematic was loaded, false otherwise.
     * @throws IOException        if an I/O error occurs while loading the schematic file.
     * @throws WorldEditException if an error occurs while using WorldEdit API to load the schematic.
     */
    boolean loadSchematic() throws IOException, WorldEditException;
}
