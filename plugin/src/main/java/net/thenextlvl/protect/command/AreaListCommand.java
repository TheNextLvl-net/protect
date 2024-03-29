package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GlobalArea;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
class AreaListCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("list").handler(this::execute);
    }

    private void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var globalAreas = plugin.areaProvider().getAreas()
                .filter(area -> area instanceof GlobalArea)
                .map(Area::getName)
                .toList();
        var userAreas = plugin.areaProvider().getAreas()
                .filter(area -> !(area instanceof GlobalArea))
                .map(Area::getName).toList();
        plugin.bundle().sendMessage(sender, "area.list",
                Placeholder.parsed("amount", String.valueOf(userAreas.size())),
                Placeholder.parsed("areas", String.join(", ", userAreas)));
        plugin.bundle().sendMessage(sender, "area.list.global",
                Placeholder.parsed("amount", String.valueOf(globalAreas.size())),
                Placeholder.parsed("areas", String.join(", ", globalAreas)));
    }
}
