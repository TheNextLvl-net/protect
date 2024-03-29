package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.WorldEditException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.CraftRegionizedArea;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

@RequiredArgsConstructor
class AreaSchematicCommand {
    private final ProtectPlugin plugin;

    @Getter
    @RequiredArgsConstructor
    public enum Option {
        LOAD("load", () -> CraftRegionizedArea.areas().filter(area ->
                !area.isGlobalArea() && area.getSchematic().getFile().isFile()).map(CraftRegionizedArea::getName).toList()),
        DELETE("delete", () -> CraftRegionizedArea.areas().filter(area ->
                !area.isGlobalArea() && area.getSchematic().getFile().isFile()).map(CraftRegionizedArea::getName).toList()),
        SAVE("save", () -> CraftRegionizedArea.areas().filter(area -> !area.isGlobalArea()).map(CraftRegionizedArea::getName).toList());

        private final String name;
        @Accessors(fluent = true)
        private final Supplier<List<String>> possibilities;

        @Nullable
        public static Option parse(String name) {
            for (Option option : values()) if (option.getName().equalsIgnoreCase(name)) return option;
            return null;
        }
    }

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("schematic")
                .argument(StringArgument.<CommandSender>builder("option")
                        .withSuggestionsProvider((context, token) ->
                                Arrays.stream(Option.values()).map(Option::getName).toList())
                        .build())
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> {
                            Option option = Option.parse(context.get("option"));
                            return option != null ? option.possibilities().get() : Collections.emptyList();
                        })
                        .build())
                .senderType(Player.class)
                .handler(AreaSchematicCommand::execute);
    }

    private void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var option = Option.parse(context.get("option"));
        var area = CraftRegionizedArea.get(context.<String>get("area"));
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        if (area != null && option != null && option.equals(Option.LOAD))
            handleLoad(sender, area, locale);
        else if (area != null && option != null && option.equals(Option.DELETE))
            handleDelete(sender, area, locale);
        else if (area != null && option != null && option.equals(Option.SAVE))
            handleSave(sender, area, locale);
        else sender.sendRichMessage(JavaPlugin.getPlugin(ProtectPlugin.class).formatter().format(
                    "%prefix% <red>/area schematic <dark_gray>[<gold>option<dark_gray>] [<gold>area<dark_gray>]"
            ));
    }

    private void handleSave(CommandSender sender, RegionizedArea<?> area, Locale locale) {
        if (area.isTooBig()) sender.sendRichMessage(Messages.AREA_WARNING_SIZE.message(locale, sender));
        var schematic = Placeholder.<Audience>of("schematic", area.getName());
        boolean save = false;
        try {
            save = area.getSchematic().save();
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
        var message = save ? Messages.SCHEMATIC_SAVE_SUCCEEDED : Messages.SCHEMATIC_SAVE_FAILED;
        sender.sendRichMessage(message.message(locale, sender, schematic));
    }

    private void handleDelete(CommandSender sender, RegionizedArea<?> area, Locale locale) {
        var schematic = Placeholder.<Audience>of("schematic", area.getName());
        var delete = area.getSchematic().delete();
        var message = delete ? Messages.SCHEMATIC_DELETE_SUCCEEDED : Messages.SCHEMATIC_DELETE_FAILED;
        sender.sendRichMessage(message.message(locale, sender, schematic));
    }

    private void handleLoad(CommandSender sender, RegionizedArea<?> area, Locale locale) {
        if (area.isTooBig()) sender.sendRichMessage(Messages.AREA_WARNING_SIZE.message(locale, sender));
        var schematic = Placeholder.<Audience>of("schematic", area.getName());
        boolean load = false;
        try {
            load = area.getSchematic().load();
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
        var message = load ? Messages.SCHEMATIC_LOAD_SUCCEEDED : Messages.SCHEMATIC_LOAD_FAILED;
        sender.sendRichMessage(message.message(locale, sender, schematic));
    }
}
