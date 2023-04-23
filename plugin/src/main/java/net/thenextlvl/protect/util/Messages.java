package net.thenextlvl.protect.util;

import core.annotation.FieldsAreNonnullByDefault;
import core.api.file.format.MessageFile;
import core.api.placeholder.MessageKey;
import core.api.placeholder.Placeholder;
import core.api.placeholder.SystemMessageKey;
import net.thenextlvl.protect.Protect;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

@FieldsAreNonnullByDefault
public class Messages {
    private static final Protect plugin = JavaPlugin.getPlugin(Protect.class);
    public static final SystemMessageKey<Void> PREFIX = new SystemMessageKey<>("protect.prefix", Placeholder.Formatter.DEFAULT).register();

    public static final MessageKey<Player> AREA_CREATED = new MessageKey<>("area.created", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_LIST = new MessageKey<>("area.list", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_LIST_GLOBAL = new MessageKey<>("area.list.global", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_EXISTS = new MessageKey<>("area.exists", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_NOT_FOUND = new MessageKey<>("area.exists.not", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_INFO_NAME = new MessageKey<>("area.info.name", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_INFO_FLAG = new MessageKey<>("area.info.flag", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_INFO_FLAG_VALUE = new MessageKey<>("area.info.flag.value", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_INFO_FLAG_DEFAULT = new MessageKey<>("area.info.flag.default", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_INFO_WORLD = new MessageKey<>("area.info.world", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_INFO_BOUNDS = new MessageKey<>("area.info.bounds", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_INFO_PRIORITY = new MessageKey<>("area.info.priority", plugin.formatter()).register();

    public static final MessageKey<Player> SCHEMATIC_EXISTS = new MessageKey<>("area.schematic.exists", plugin.formatter()).register();
    public static final MessageKey<Player> SCHEMATIC_DELETE_SUCCEEDED = new MessageKey<>("area.schematic.delete.success", plugin.formatter()).register();
    public static final MessageKey<Player> SCHEMATIC_DELETE_FAILED = new MessageKey<>("area.schematic.delete.failed", plugin.formatter()).register();
    public static final MessageKey<Player> SCHEMATIC_SAVE_SUCCEEDED = new MessageKey<>("area.schematic.save.success", plugin.formatter()).register();
    public static final MessageKey<Player> SCHEMATIC_SAVE_FAILED = new MessageKey<>("area.schematic.save.failed", plugin.formatter()).register();
    public static final MessageKey<Player> SCHEMATIC_LOAD_SUCCEEDED = new MessageKey<>("area.schematic.load.success", plugin.formatter()).register();
    public static final MessageKey<Player> SCHEMATIC_LOAD_FAILED = new MessageKey<>("area.schematic.load.failed", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_FLAG_CHANGED = new MessageKey<>("area.flag.changed", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_FLAG_UNSET = new MessageKey<>("area.flag.unset", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_PRIORITY_CHANGED = new MessageKey<>("area.priority.changed", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_WARNING_SIZE = new MessageKey<>("area.warning.size", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_SELECTED = new MessageKey<>("area.selected", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_DELETE_SUCCEEDED = new MessageKey<>("area.delete.success", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_DELETE_FAILED = new MessageKey<>("area.delete.failed", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_REDEFINE_SUCCEEDED = new MessageKey<>("area.redefine.success", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_REDEFINE_FAILED = new MessageKey<>("area.redefine.fail", plugin.formatter()).register();

    public static final MessageKey<Player> AREA_TELEPORT_SUCCEEDED = new MessageKey<>("area.teleport.success", plugin.formatter()).register();
    public static final MessageKey<Player> AREA_TELEPORT_FAILED = new MessageKey<>("area.teleport.fail", plugin.formatter()).register();

    public static final MessageKey<Player> SELECT_REGION = new MessageKey<>("region.select", plugin.formatter()).register();

    public static final MessageKey<Player> INVALID_AREA = new MessageKey<>("area.invalid", plugin.formatter()).register();

    public static final MessageKey<Player> NO_PERMISSION = new MessageKey<>("command.permission", plugin.formatter()).register();
    public static final MessageKey<Player> COMMAND_EXCEPTION = new MessageKey<>("command.exception", plugin.formatter()).register();
    public static final MessageKey<Player> INVALID_SENDER = new MessageKey<>("command.sender", plugin.formatter()).register();

    public static final MessageKey<Player> NOTHING_CHANGED = new MessageKey<>("nothing.changed", plugin.formatter()).register();

    public static void init() {
        initRoot();
        initEnglish();
        initGerman();
    }

    private static void initRoot() {
        var file = MessageFile.ROOT;
        file.setDefault(PREFIX, "<white>Protect <dark_gray>»<reset>");
        file.save();
    }

    private static void initEnglish() {
        var file = MessageFile.getOrCreate(Locale.forLanguageTag("en-US"));
        file.setDefault(AREA_CREATED, "%prefix% <white>Created area called <green>%area%");
        file.setDefault(AREA_LIST, "%prefix% <gray>Areas <dark_gray>(<green>%amount%<dark_gray>): <white>%areas%");
        file.setDefault(AREA_LIST_GLOBAL, "%prefix% <gray>Global <dark_gray>(<green>%amount%<dark_gray>): <white>%areas%");
        file.setDefault(AREA_EXISTS, "%prefix% <red>An area called <dark_red>%area%<red> does already exist");
        file.setDefault(AREA_NOT_FOUND, "%prefix% <red>An area called <dark_red>%area%<red> does not exist");
        file.setDefault(AREA_DELETE_SUCCEEDED, "%prefix% <white>Successfully deleted the area <green>%area%");
        file.setDefault(AREA_DELETE_FAILED, "%prefix% <red>Failed to deleted the area <dark_red>%area%");
        file.setDefault(AREA_INFO_NAME, "%prefix% <gray>Area<dark_gray>: <white>%area%");
        file.setDefault(AREA_INFO_FLAG, "%prefix% <gray>Flag<dark_gray>: <white>%flag%");
        file.setDefault(AREA_INFO_FLAG_VALUE, "%prefix% <gray>Value<dark_gray>: <white>%flag%");
        file.setDefault(AREA_INFO_FLAG_DEFAULT, "%prefix% <gray>Default<dark_gray>: <white>%flag%");
        file.setDefault(AREA_INFO_WORLD, "%prefix% <gray>World<dark_gray>: <white>%world%");
        file.setDefault(AREA_INFO_BOUNDS, "%prefix% <gray>Bounds<dark_gray>: <white>%bounds%");
        file.setDefault(AREA_INFO_PRIORITY, "%prefix% <gray>Priority<dark_gray>: <white>%priority%");
        file.setDefault(SCHEMATIC_EXISTS, "%prefix% <red>Delete the schematic before redefining this area");
        file.setDefault(SCHEMATIC_DELETE_SUCCEEDED, "%prefix% <white>Deleted schematic <green>%schematic%");
        file.setDefault(SCHEMATIC_DELETE_FAILED, "%prefix% <red>Failed to delete schematic <dark_red>%schematic%");
        file.setDefault(SCHEMATIC_SAVE_SUCCEEDED, "%prefix% <white>Saved schematic <green>%schematic%");
        file.setDefault(SCHEMATIC_SAVE_FAILED, "%prefix% <red>Failed to save schematic <dark_red>%schematic%");
        file.setDefault(SCHEMATIC_LOAD_SUCCEEDED, "%prefix% <white>Loaded schematic <green>%schematic%");
        file.setDefault(SCHEMATIC_LOAD_FAILED, "%prefix% <red>Failed to load schematic <dark_red>%schematic%");
        file.setDefault(AREA_FLAG_CHANGED, "%prefix% <white>Changed flag <green>%flag%<white> of area <green>%area%<white> to <green>%value%");
        file.setDefault(AREA_FLAG_UNSET, "%prefix% <white>Unset flag <green>%flag%<white> of the area <green>%area%");
        file.setDefault(AREA_PRIORITY_CHANGED, "%prefix% <white>Changed priority of area <green>%area%<white> to <green>%priority%");
        file.setDefault(AREA_WARNING_SIZE, "%prefix% <gray>Attention<dark_gray>: <white>The area is very big");
        file.setDefault(AREA_TELEPORT_SUCCEEDED, "%prefix% <white>Teleported you to area <green>%area%");
        file.setDefault(AREA_TELEPORT_FAILED, "%prefix% <red>Failed to teleport you to area <dark_red>%area%");
        file.setDefault(AREA_REDEFINE_SUCCEEDED, "%prefix% <gray>Redefined area<dark_gray>: <white>%area%");
        file.setDefault(AREA_REDEFINE_FAILED, "%prefix% <red>Failed to redefine area <dark_red>%area%");
        file.setDefault(SELECT_REGION, "%prefix% <red>Select a <dark_gray>(<dark_red>WorldEdit<dark_gray>)<red> region first");
        file.setDefault(AREA_SELECTED, "%prefix% <gray>Selected area<dark_gray>: <white>%area%");
        file.setDefault(INVALID_AREA, "%prefix% <red>Can't select a global area");
        file.setDefault(NO_PERMISSION, "%prefix% <red>You have no rights <dark_gray>(<dark_red>%permission%<dark_gray>)");
        file.setDefault(COMMAND_EXCEPTION, "%prefix% <red>Failed to execute command <dark_gray>(<dark_red>%command%<dark_gray>)");
        file.setDefault(INVALID_SENDER, "%prefix% <red>You cannot use this command");
        file.setDefault(NOTHING_CHANGED, "%prefix% <red>Nothing could be changed");
        file.save();
    }

    private static void initGerman() {
        var file = MessageFile.getOrCreate(Locale.forLanguageTag("de-DE"));
        file.setDefault(AREA_CREATED, "%prefix% <white>Der Bereich <green>%area%<white> wurde erstellt");
        file.setDefault(AREA_LIST, "%prefix% <gray>Bereiche <dark_gray>(<green>%amount%<dark_gray>): <white>%areas%");
        file.setDefault(AREA_LIST_GLOBAL, "%prefix% <gray>Global<dark_gray>(<green>%amount%<dark_gray>): <white>%areas%");
        file.setDefault(AREA_EXISTS, "%prefix% <red>Ein Bereich mit dem namen <dark_red>%area%<red> existiert bereits");
        file.setDefault(AREA_NOT_FOUND, "%prefix% <red>Ein Bereich mit dem namen <dark_red>%area%<red> existiert nicht");
        file.setDefault(AREA_DELETE_SUCCEEDED, "%prefix% <white>Der Bereich <green>%area%<white> wurde erfolgreich gelöscht");
        file.setDefault(AREA_DELETE_FAILED, "%prefix% <red>Der Bereich <green>%area%<red> konnte nicht gelöscht werden");
        file.setDefault(AREA_INFO_NAME, "%prefix% <gray>Bereich<dark_gray>: <white>%area%");
        file.setDefault(AREA_INFO_FLAG, "%prefix% <gray>Flagge<dark_gray>: <white>%flag%");
        file.setDefault(AREA_INFO_FLAG_VALUE, "%prefix% <gray>Wert<dark_gray>: <white>%flag%");
        file.setDefault(AREA_INFO_FLAG_DEFAULT, "%prefix% <gray>Standard<dark_gray>: <white>%flag%");
        file.setDefault(AREA_INFO_WORLD, "%prefix% <gray>Welt<dark_gray>: <white>%world%");
        file.setDefault(AREA_INFO_BOUNDS, "%prefix% <gray>Grenzen<dark_gray>: <white>%bounds%");
        file.setDefault(AREA_INFO_PRIORITY, "%prefix% <gray>Priorität<dark_gray>: <white>%priority%");
        file.setDefault(SCHEMATIC_EXISTS, "%prefix% <red>Lösche die schematic bevor du diesen Bereich neu definierst");
        file.setDefault(SCHEMATIC_DELETE_SUCCEEDED, "%prefix% <white>Die schematic <green>%schematic%<white> wurde erfolgreich gelöscht");
        file.setDefault(SCHEMATIC_DELETE_FAILED, "%prefix% <red>Die schematic <dark_red>%schematic%<red> konnte nicht gelöscht werden");
        file.setDefault(SCHEMATIC_SAVE_SUCCEEDED, "%prefix% <white>Die schematic <green>%schematic%<white> wurde erfolgreich gespeichert");
        file.setDefault(SCHEMATIC_SAVE_FAILED, "%prefix% <red>Die schematic <dark_red>%schematic%<red> konnte nicht gespeichert werden");
        file.setDefault(SCHEMATIC_LOAD_SUCCEEDED, "%prefix% <white>Die schematic <green>%schematic%<white> wurde erfolgreich geladen");
        file.setDefault(SCHEMATIC_LOAD_FAILED, "%prefix% <red>Die schematic <dark_red>%schematic%<red> konnte nicht geladen werden");
        file.setDefault(AREA_FLAG_CHANGED, "%prefix% <white>Die Flagge <green>%flag%<white> vom Bereich <green>%area%<white> wurde auf <green>%value%<white> gesetzt");
        file.setDefault(AREA_FLAG_UNSET, "%prefix% <white>Die Flagge <green>%flag%<white> vom Bereich <green>%area%<white> wurde zurückgesetzt");
        file.setDefault(AREA_PRIORITY_CHANGED, "%prefix% <white>Die priorität vom Bereich <green>%area%<white> wurde auf <green>%priority%<white> gesetzt");
        file.setDefault(AREA_WARNING_SIZE, "%prefix% <gray>Achtung<dark_gray>: <white>Der Bereich ist sehr groß");
        file.setDefault(AREA_TELEPORT_SUCCEEDED, "%prefix% <white>Du wurdest zum Bereich <green>%area%<white> teleportiert");
        file.setDefault(AREA_TELEPORT_FAILED, "%prefix% <red>Du konntest nicht zum Bereich <dark_red>%area%<red> teleportiert werden");
        file.setDefault(AREA_REDEFINE_SUCCEEDED, "%prefix% <gray>Bereich neudefiniert<dark_gray>: <white>%area%");
        file.setDefault(AREA_REDEFINE_FAILED, "%prefix% <red>Der Bereich <dark_red>%area%<red> konnte nicht neudefiniert werden");
        file.setDefault(SELECT_REGION, "%prefix% <red>Wähle erst eine <dark_gray>(<dark_red>WorldEdit<dark_gray>)<red> region aus");
        file.setDefault(AREA_SELECTED, "%prefix% <gray>Bereich ausgewählt<dark_gray>: <white>%area%");
        file.setDefault(INVALID_AREA, "%prefix% <red>Globale bereiche können nicht ausgewählt werden");
        file.setDefault(NO_PERMISSION, "%prefix%<red> Darauf hast du keine rechte <dark_gray>(<dark_red>%permission%<dark_gray>)");
        file.setDefault(COMMAND_EXCEPTION, "%prefix%<red> Fehler beim ausführen des commands <dark_gray>(<dark_red>%command%<dark_gray>)");
        file.setDefault(INVALID_SENDER, "%prefix%<red> Du kannst diesen command nicht nutzen");
        file.setDefault(NOTHING_CHANGED, "%prefix% <red>Es konnte nichts geändert werden");
        file.save();
    }
}
