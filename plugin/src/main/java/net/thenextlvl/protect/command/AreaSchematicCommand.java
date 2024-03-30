package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.WorldEditException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
class AreaSchematicCommand {
    private final ProtectPlugin plugin;

    @Getter
    @RequiredArgsConstructor
    public enum Option {
        LOAD("load"),
        DELETE("delete"),
        SAVE("save");

        private final String name;

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
                            var option = Option.parse(context.get("option"));
                            if (option == null) return Collections.emptyList();
                            return switch (option) {
                                case LOAD, DELETE -> plugin.areaProvider().getAreas()
                                        .filter(area -> area instanceof SchematicHolder holder
                                                && holder.getSchematic().isFile())
                                        .map(Area::getName)
                                        .toList();
                                case SAVE -> plugin.areaProvider().getAreas()
                                        .filter(area -> area instanceof SchematicHolder)
                                        .map(Area::getName)
                                        .toList();
                            };
                        })
                        .build())
                .senderType(Player.class)
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var option = Option.parse(context.get("option"));
        var name = context.<String>get("area");
        // todo pattern validation
        var area = plugin.areaProvider().getArea(name).orElse(null);
        if (!(area instanceof RegionizedArea<?> regionizedArea) || option == null) {
            plugin.bundle().sendMessage(sender, "command.area.schematic");
        } else switch (option) {
            case LOAD -> handleLoad(sender, regionizedArea);
            case DELETE -> handleDelete(sender, regionizedArea);
            case SAVE -> handleSave(sender, regionizedArea);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void handleSave(CommandSender sender, RegionizedArea<?> area) {
        if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
        try {
            area.saveSchematic();
            plugin.bundle().sendMessage(sender, "area.schematic.save.success",
                    Placeholder.parsed("schematic", area.getName()));
        } catch (IOException | WorldEditException e) {
            plugin.bundle().sendMessage(sender, "area.schematic.save.failed",
                    Placeholder.parsed("schematic", area.getName()));
            e.printStackTrace();
        }
    }

    private void handleDelete(CommandSender sender, RegionizedArea<?> area) {
        var message = area.deleteSchematic() ? "area.schematic.delete.success" : "area.schematic.delete.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getName()));
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void handleLoad(CommandSender sender, RegionizedArea<?> area) {
        if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
        var load = false;
        try {
            load = area.loadSchematic();
        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
        var message = load ? "area.schematic.load.success" : "area.schematic.load.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getName()));
    }
}
