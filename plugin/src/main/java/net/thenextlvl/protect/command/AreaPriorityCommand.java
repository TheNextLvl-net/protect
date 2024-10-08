package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaPriorityCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("priority")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.priority"))
                .then(Commands.argument("area", new AreaArgumentType(plugin))
                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                .executes(this::set))
                        .executes(this::get));
    }

    private int set(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        var priority = context.getArgument("priority", int.class);
        var message = area.setPriority(priority) ? "area.priority.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("priority", String.valueOf(area.getPriority())));
        return Command.SINGLE_SUCCESS;
    }

    private int get(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        plugin.bundle().sendMessage(sender, "area.info.priority",
                Placeholder.parsed("priority", String.valueOf(area.getPriority())));
        return Command.SINGLE_SUCCESS;
    }
}
