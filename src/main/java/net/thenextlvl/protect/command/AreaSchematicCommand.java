package net.thenextlvl.protect.command;

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
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;

import java.io.IOException;

class AreaSchematicCommand {
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
                                area -> area.getSchematic().exists()))
                        .executes(context -> delete(context, plugin)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> load(ProtectPlugin plugin) {
        return Commands.literal("load")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.load"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                area -> area.getSchematic().exists()))
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
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getSchematic().getName()));
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
        }
        var message = load ? "area.schematic.load.success" : "area.schematic.load.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getSchematic().getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static int save(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
        try {
            area.saveSchematic();
            plugin.bundle().sendMessage(sender, "area.schematic.save.success",
                    Placeholder.parsed("schematic", area.getSchematic().getName()),
                    Formatter.number("size", area.getSchematic().length() / 1024));
        } catch (IOException | WorldEditException e) {
            plugin.bundle().sendMessage(sender, "area.schematic.save.failed",
                    Placeholder.parsed("schematic", area.getSchematic().getName()));
            plugin.getComponentLogger().error("Failed dto save area schematic", e);
        }
        return Command.SINGLE_SUCCESS;
    }
}
