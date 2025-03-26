package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.AreaArgumentType;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import net.thenextlvl.protect.exception.CircularInheritanceException;

class AreaParentCommand {
    private final ProtectPlugin plugin;

    AreaParentCommand(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("parent")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.parent"))
                .then(Commands.literal("set")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.parent.set"))
                        .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                                .then(Commands.argument("parent", new AreaArgumentType(plugin))
                                        .executes(this::set))))
                .then(Commands.literal("remove")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.parent.remove"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin,
                                        (commandContext, area) -> area.getParent().isPresent()))
                                .executes(this::remove)));
    }

    private int set(CommandContext<CommandSourceStack> context) {
        try {
            var sender = context.getSource().getSender();
            var area = context.getArgument("area", RegionizedArea.class);
            var parent = context.getArgument("parent", Area.class);
            var success = area.setParent(parent);
            var message = success ? "area.parent.set" : "nothing.changed";
            plugin.bundle().sendMessage(sender, message,
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("parent", parent.getName()));
            return Command.SINGLE_SUCCESS;
        } catch (CircularInheritanceException e) {
            var sender = context.getSource().getSender();
            plugin.bundle().sendMessage(sender, "area.parent.circular-inheritance");
            return 0;
        }
    }

    private int remove(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        var success = area.setParent(null);
        var message = success ? "area.parent.remove" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
