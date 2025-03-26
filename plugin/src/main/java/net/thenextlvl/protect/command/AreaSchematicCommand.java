package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.WorldEditException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;

import java.io.IOException;

class AreaSchematicCommand {
    private final ProtectPlugin plugin;

    AreaSchematicCommand(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("schematic")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic"))
                .then(delete())
                .then(load())
                .then(save());
    }

    private ArgumentBuilder<CommandSourceStack, ?> delete() {
        return Commands.literal("delete")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.delete"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                area -> area.getSchematic().exists()))
                        .executes(this::delete));
    }

    private ArgumentBuilder<CommandSourceStack, ?> load() {
        return Commands.literal("load")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.load"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                area -> area.getSchematic().exists()))
                        .executes(this::load));
    }

    private ArgumentBuilder<CommandSourceStack, ?> save() {
        return Commands.literal("save")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.schematic.save"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(this::save));
    }

    private int delete(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        var message = area.deleteSchematic() ? "area.schematic.delete.success" : "area.schematic.delete.failed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getSchematic().getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int load(CommandContext<CommandSourceStack> context) {
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

    private int save(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
        try {
            area.saveSchematic();
            plugin.bundle().sendMessage(sender, "area.schematic.save.success",
                    Placeholder.parsed("schematic", area.getSchematic().getName()),
                    Placeholder.parsed("size", String.valueOf(area.getSchematic().length() / 1024)));
        } catch (IOException | WorldEditException e) {
            plugin.bundle().sendMessage(sender, "area.schematic.save.failed",
                    Placeholder.parsed("schematic", area.getSchematic().getName()));
            plugin.getComponentLogger().error("Failed dto save area schematic", e);
        }
        return Command.SINGLE_SUCCESS;
    }
}
