package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GlobalArea;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaListCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("list")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.list"))
                .executes(this::list);
    }

    private int list(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var globalAreas = plugin.areaProvider().getAreas()
                .filter(area -> area instanceof GlobalArea)
                .map(Area::getName)
                .toList();
        var userAreas = plugin.areaProvider().getAreas()
                .filter(area -> !(area instanceof GlobalArea))
                .map(Area::getName).toList();
        if (!userAreas.isEmpty()) plugin.bundle().sendMessage(sender, "area.list",
                Placeholder.parsed("amount", String.valueOf(userAreas.size())),
                Placeholder.parsed("areas", String.join(", ", userAreas)));
        plugin.bundle().sendMessage(sender, "area.list.global",
                Placeholder.parsed("amount", String.valueOf(globalAreas.size())),
                Placeholder.parsed("areas", String.join(", ", globalAreas)));
        return Command.SINGLE_SUCCESS;
    }
}
