package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.WorldEditException;
import core.api.placeholder.Placeholder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

class AreaSchematicCommand {

    @Getter
    @RequiredArgsConstructor
    public enum Option {
        LOAD("load", () -> Area.areas().filter(area ->
                !area.isGlobalArea() && area.getSchematic().getFile().isFile()).map(Area::getName).toList()),
        DELETE("delete", () -> Area.areas().filter(area ->
                !area.isGlobalArea() && area.getSchematic().getFile().isFile()).map(Area::getName).toList()),
        SAVE("save", () -> Area.areas().filter(area -> !area.isGlobalArea()).map(Area::getName).toList());

        private final String name;
        @Accessors(fluent = true)
        private final Supplier<List<String>> possibilities;

        @Nullable
        public static Option parse(String name) {
            for (Option option : values()) if (option.getName().equalsIgnoreCase(name)) return option;
            return null;
        }
    }

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
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

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var option = Option.parse(context.get("option"));
        var area = Area.get(context.<String>get("area"));
        var plugin = JavaPlugin.getPlugin(Protect.class);
        if (area != null && option != null && option.equals(Option.LOAD)) {
            if (area.isTooBig())
                player.sendRichMessage(Messages.AREA_WARNING_SIZE.message(player.locale(), player));
            var schematic = Placeholder.<Player>of("schematic", area.getName());
            boolean load = false;
            try {
                load = area.getSchematic().load();
            } catch (IOException | WorldEditException e) {
                e.printStackTrace();
            }
            var message = load ? Messages.SCHEMATIC_LOAD_SUCCEEDED : Messages.SCHEMATIC_LOAD_FAILED;
            player.sendRichMessage(message.message(player.locale(), player, schematic));
        } else if (area != null && option != null && option.equals(Option.DELETE)) {
            var schematic = Placeholder.<Player>of("schematic", area.getName());
            var delete = area.getSchematic().delete();
            var message = delete ? Messages.SCHEMATIC_DELETE_SUCCEEDED : Messages.SCHEMATIC_DELETE_FAILED;
            player.sendRichMessage(message.message(player.locale(), player, schematic));
        } else if (area != null && option != null && option.equals(Option.SAVE)) {
            if (area.isTooBig())
                player.sendRichMessage(Messages.AREA_WARNING_SIZE.message(player.locale(), player));
            var schematic = Placeholder.<Player>of("schematic", area.getName());
            boolean save = false;
            try {
                save = area.getSchematic().save();
            } catch (IOException | WorldEditException e) {
                e.printStackTrace();
            }
            var message = save ? Messages.SCHEMATIC_SAVE_SUCCEEDED : Messages.SCHEMATIC_SAVE_FAILED;
            player.sendRichMessage(message.message(player.locale(), player, schematic));
        } else player.sendRichMessage(plugin.formatter().format(
                "%prefix% §c/area schematic §8[§6option§8] [§6area§8]"
        ));
    }
}
