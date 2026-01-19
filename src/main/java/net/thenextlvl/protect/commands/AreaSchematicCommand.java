package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.WorldEditException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.commands.argument.RegionizedAreaArgumentType;
import org.jspecify.annotations.NullMarked;

import java.io.IOException;
import java.nio.file.Files;

import static net.thenextlvl.protect.ProtectPlugin.ISSUES;

@NullMarked
final class AreaSchematicCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("schematic")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic"))
                .then(delete(plugin))
                .then(load(plugin))
                .then(save(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> delete(ProtectPlugin plugin) {
        return Commands.literal("delete")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.delete"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                area -> Files.isRegularFile(area.getSchematicFile())))
                        .executes(context -> delete(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> load(ProtectPlugin plugin) {
        return Commands.literal("load")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.load"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                area -> Files.isRegularFile(area.getSchematicFile())))
                        .executes(context -> load(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> save(ProtectPlugin plugin) {
        return Commands.literal("save")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.save"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(context -> save(context, plugin)));
    }

    private static int delete(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        var message = area.deleteSchematic() ? "area.schematic.delete.success" : "area.schematic.delete.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getSchematicFile().getFileName().toString()));
        return Command.SINGLE_SUCCESS;
    }

    private static int load(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
        var load = false;
        try {
            load = area.loadSchematic();
        } catch (IOException | WorldEditException e) {
            plugin.getComponentLogger().error("Failed to load area schematic", e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
        }
        var message = load ? "area.schematic.load.success" : "area.schematic.load.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getSchematicFile().getFileName().toString()));
        return Command.SINGLE_SUCCESS;
    }

    private static int save(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
        try {
            area.saveSchematic();
            var size = 0L;
            try {
                size = Files.size(area.getSchematicFile()) / 1024;
            } catch (IOException e) {
                plugin.getComponentLogger().warn("Failed to get file size of area {}", area.getName(), e);
            }
            plugin.bundle().sendMessage(sender, "area.schematic.save.success",
                    Placeholder.parsed("schematic", area.getSchematicFile().getFileName().toString()),
                    Formatter.number("size", size));
        } catch (IOException | WorldEditException e) {
            plugin.bundle().sendMessage(sender, "area.schematic.save.failed",
                    Placeholder.parsed("schematic", area.getSchematicFile().getFileName().toString()));
            plugin.getComponentLogger().error("Failed to save area schematic", e);
            plugin.getComponentLogger().error("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
        }
        return Command.SINGLE_SUCCESS;
    }
}
