package net.thenextlvl.protect.util;

import core.annotation.FieldsAreNonnullByDefault;
import core.api.file.format.MessageFile;
import core.api.placeholder.MessageKey;
import core.api.placeholder.Placeholder;
import core.api.placeholder.SystemMessageKey;
import net.thenextlvl.protect.Protect;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
        file.setDefault(PREFIX, "§fProtect §8»§r");
        file.save();
    }

    private static void initEnglish() {
        var file = MessageFile.getOrCreate(MessageFile.parseLocale("en_us"));
        file.setDefault(AREA_CREATED, "%prefix% §fCreated area called §a%area%");
        file.setDefault(AREA_LIST, "%prefix% §7Areas §8(§a%amount%§8): §f%areas%");
        file.setDefault(AREA_LIST_GLOBAL, "%prefix% §7Global §8(§a%amount%§8): §f%areas%");
        file.setDefault(AREA_EXISTS, "%prefix% §cAn area called §4%area%§c does already exist");
        file.setDefault(AREA_NOT_FOUND, "%prefix% §cAn area called §4%area%§c does not exist");
        file.setDefault(AREA_DELETE_SUCCEEDED, "%prefix% §fSuccessfully deleted the area §a%area%");
        file.setDefault(AREA_DELETE_FAILED, "%prefix% §cFailed to deleted the area §4%area%");
        file.setDefault(AREA_INFO_NAME, "%prefix% §7Area§8: §f%area%");
        file.setDefault(AREA_INFO_FLAG, "%prefix% §7Flag§8: §f%flag%");
        file.setDefault(AREA_INFO_FLAG_VALUE, "%prefix% §7Value§8: §f%flag%");
        file.setDefault(AREA_INFO_FLAG_DEFAULT, "%prefix% §7Default§8: §f%flag%");
        file.setDefault(AREA_INFO_WORLD, "%prefix% §7World§8: §f%world%");
        file.setDefault(AREA_INFO_BOUNDS, "%prefix% §7Bounds§8: §f%bounds%");
        file.setDefault(AREA_INFO_PRIORITY, "%prefix% §7Priority§8: §f%priority%");
        file.setDefault(SCHEMATIC_EXISTS, "%prefix% §cDelete the schematic before redefining this area");
        file.setDefault(SCHEMATIC_DELETE_SUCCEEDED, "%prefix% §fDeleted schematic §a%schematic%");
        file.setDefault(SCHEMATIC_DELETE_FAILED, "%prefix% §cFailed to delete schematic §4%schematic%");
        file.setDefault(SCHEMATIC_SAVE_SUCCEEDED, "%prefix% §fSaved schematic §a%schematic%");
        file.setDefault(SCHEMATIC_SAVE_FAILED, "%prefix% §cFailed to save schematic §4%schematic%");
        file.setDefault(SCHEMATIC_LOAD_SUCCEEDED, "%prefix% §fLoaded schematic §a%schematic%");
        file.setDefault(SCHEMATIC_LOAD_FAILED, "%prefix% §cFailed to load schematic §4%schematic%");
        file.setDefault(AREA_FLAG_CHANGED, "%prefix% §fChanged flag §a%flag%§f of area §a%area%§f to §a%value%");
        file.setDefault(AREA_FLAG_UNSET, "%prefix% §fUnset flag §a%flag%§f of the area §a%area%");
        file.setDefault(AREA_PRIORITY_CHANGED, "%prefix% §fChanged priority of area §a%area%§f to §a%priority%");
        file.setDefault(AREA_WARNING_SIZE, "%prefix% §7Attention§8: §fThe area is very big");
        file.setDefault(AREA_TELEPORT_SUCCEEDED, "%prefix% §fTeleported you to area §a%area%");
        file.setDefault(AREA_TELEPORT_FAILED, "%prefix% §cFailed to teleport you to area §4%area%");
        file.setDefault(AREA_REDEFINE_SUCCEEDED, "%prefix% §7Redefined area§8: §f%area%");
        file.setDefault(AREA_REDEFINE_FAILED, "%prefix% §cFailed to redefine area §4%area%");
        file.setDefault(SELECT_REGION, "%prefix% §cSelect a §8(§4WorldEdit§8)§c region first");
        file.setDefault(AREA_SELECTED, "%prefix% §7Selected area§8: §f%area%");
        file.setDefault(INVALID_AREA, "%prefix% §cCan't select a global area");
        file.setDefault(NO_PERMISSION, "%prefix% §cYou have no rights §8(§4%permission%§8)");
        file.setDefault(COMMAND_EXCEPTION, "%prefix% §cFailed to execute command §8(§4%command%§8)");
        file.setDefault(INVALID_SENDER, "%prefix% §cYou cannot use this command");
        file.setDefault(NOTHING_CHANGED, "%prefix% §cNothing could be changed");
        file.save();
    }

    private static void initGerman() {
        var file = MessageFile.getOrCreate(MessageFile.parseLocale("de_de"));
        file.setDefault(AREA_CREATED, "%prefix% §fDer Bereich §a%area%§f wurde erstellt");
        file.setDefault(AREA_LIST, "%prefix% §7Bereiche §8(§a%amount%§8): §f%areas%");
        file.setDefault(AREA_LIST_GLOBAL, "%prefix% §7Global§8(§a%amount%§8): §f%areas%");
        file.setDefault(AREA_EXISTS, "%prefix% §cEin Bereich mit dem namen §4%area%§c existiert bereits");
        file.setDefault(AREA_NOT_FOUND, "%prefix% §cEin Bereich mit dem namen §4%area%§c existiert nicht");
        file.setDefault(AREA_DELETE_SUCCEEDED, "%prefix% §fDer Bereich §a%area%§f wurde erfolgreich gelöscht");
        file.setDefault(AREA_DELETE_FAILED, "%prefix% §cDer Bereich §a%area%§c konnte nicht gelöscht werden");
        file.setDefault(AREA_INFO_NAME, "%prefix% §7Bereich§8: §f%area%");
        file.setDefault(AREA_INFO_FLAG, "%prefix% §7Flagge§8: §f%flag%");
        file.setDefault(AREA_INFO_FLAG_VALUE, "%prefix% §7Wert§8: §f%flag%");
        file.setDefault(AREA_INFO_FLAG_DEFAULT, "%prefix% §7Standard§8: §f%flag%");
        file.setDefault(AREA_INFO_WORLD, "%prefix% §7Welt§8: §f%world%");
        file.setDefault(AREA_INFO_BOUNDS, "%prefix% §7Grenzen§8: §f%bounds%");
        file.setDefault(AREA_INFO_PRIORITY, "%prefix% §7Priorität§8: §f%priority%");
        file.setDefault(SCHEMATIC_EXISTS, "%prefix% §cLösche die schematic bevor du diesen Bereich neu definierst");
        file.setDefault(SCHEMATIC_DELETE_SUCCEEDED, "%prefix% §fDie schematic §a%schematic%§f wurde erfolgreich gelöscht");
        file.setDefault(SCHEMATIC_DELETE_FAILED, "%prefix% §cDie schematic §4%schematic%§c konnte nicht gelöscht werden");
        file.setDefault(SCHEMATIC_SAVE_SUCCEEDED, "%prefix% §fDie schematic §a%schematic%§f wurde erfolgreich gespeichert");
        file.setDefault(SCHEMATIC_SAVE_FAILED, "%prefix% §cDie schematic §4%schematic%§c konnte nicht gespeichert werden");
        file.setDefault(SCHEMATIC_LOAD_SUCCEEDED, "%prefix% §fDie schematic §a%schematic%§f wurde erfolgreich geladen");
        file.setDefault(SCHEMATIC_LOAD_FAILED, "%prefix% §cDie schematic §4%schematic%§c konnte nicht geladen werden");
        file.setDefault(AREA_FLAG_CHANGED, "%prefix% §fDie Flagge §a%flag%§f vom Bereich §a%area%§f wurde auf §a%value%§f gesetzt");
        file.setDefault(AREA_FLAG_UNSET, "%prefix% §fDie Flagge §a%flag%§f vom Bereich §a%area%§f wurde zurückgesetzt");
        file.setDefault(AREA_PRIORITY_CHANGED, "%prefix% §fDie priorität vom Bereich §a%area%§f wurde auf §a%priority%§f gesetzt");
        file.setDefault(AREA_WARNING_SIZE, "%prefix% §7Achtung§8: §fDer Bereich ist sehr groß");
        file.setDefault(AREA_TELEPORT_SUCCEEDED, "%prefix% §fDu wurdest zum Bereich §a%area%§f teleportiert");
        file.setDefault(AREA_TELEPORT_FAILED, "%prefix% §cDu konntest nicht zum Bereich §4%area%§c teleportiert werden");
        file.setDefault(AREA_REDEFINE_SUCCEEDED, "%prefix% §7Bereich neudefiniert§8: §f%area%");
        file.setDefault(AREA_REDEFINE_FAILED, "%prefix% §cDer Bereich §4%area%§c konnte nicht neudefiniert werden");
        file.setDefault(SELECT_REGION, "%prefix% §cWähle erst eine §8(§4WorldEdit§8)§c region aus");
        file.setDefault(AREA_SELECTED, "%prefix% §7Bereich ausgewählt§8: §f%area%");
        file.setDefault(INVALID_AREA, "%prefix% §cGlobale bereiche können nicht ausgewählt werden");
        file.setDefault(NO_PERMISSION, "%prefix%§c Darauf hast du keine rechte §8(§4%permission%§8)");
        file.setDefault(COMMAND_EXCEPTION, "%prefix%§c Fehler beim ausführen des commands §8(§4%command%§8)");
        file.setDefault(INVALID_SENDER, "%prefix%§c Du kannst diesen command nicht nutzen");
        file.setDefault(NOTHING_CHANGED, "%prefix% §cEs konnte nichts geändert werden");
        file.save();
    }
}
