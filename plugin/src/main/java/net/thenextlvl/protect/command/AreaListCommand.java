package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GlobalArea;

class AreaListCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("list")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.list"))
                .executes(context -> list(context, plugin));
    }

    private static int list(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var globalAreas = plugin.areaProvider().getAreas()
                .filter(GlobalArea.class::isInstance)
                .map(Area::getName)
                .map(Component::text)
                .toList();
        var userAreas = plugin.areaProvider().getAreas()
                .filter(area -> !(area instanceof GlobalArea))
                .map(Area::getName)
                .map(Component::text)
                .toList();
        if (!userAreas.isEmpty()) plugin.bundle().sendMessage(sender, "area.list",
                Formatter.number("amount", userAreas.size()),
                Formatter.joining("areas", userAreas));
        plugin.bundle().sendMessage(sender, "area.list.global",
                Formatter.number("amount", globalAreas.size()),
                Formatter.joining("areas", globalAreas));
        return Command.SINGLE_SUCCESS;
    }
}
