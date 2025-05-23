package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;

class AreaPriorityCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("priority")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.priority"))
                .then(Commands.argument("area", new AreaArgumentType(plugin))
                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                .executes(context -> set(context, plugin)))
                        .executes(context -> get(context, plugin)));
    }

    private static int set(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        var priority = context.getArgument("priority", int.class);
        var message = area.setPriority(priority) ? "area.priority.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("area", area.getName()),
                Formatter.number("priority", area.getPriority()));
        return Command.SINGLE_SUCCESS;
    }

    private static int get(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        plugin.bundle().sendMessage(sender, "area.priority",
                Placeholder.parsed("area", area.getName()),
                Formatter.number("priority", area.getPriority()));
        return Command.SINGLE_SUCCESS;
    }
}
