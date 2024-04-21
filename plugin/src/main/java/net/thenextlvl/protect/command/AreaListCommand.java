package net.thenextlvl.protect.command;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GlobalArea;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;

@RequiredArgsConstructor
class AreaListCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSender> builder;

    Command.Builder<CommandSender> create() {
        return builder.literal("list")
                .permission("protect.command.area.list")
                .commandDescription(Description.description("List all areas"))
                .handler(this::execute);
    }

    private void execute(CommandContext<CommandSender> context) {
        var sender = context.sender();
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
    }
}
