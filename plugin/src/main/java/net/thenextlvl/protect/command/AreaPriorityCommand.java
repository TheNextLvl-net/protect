package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class AreaPriorityCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("priority")
                .argument(IntegerArgument.of("priority"))
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().map(Area::getDisplayName).filter(s ->
                                s.startsWith(token)).toList())
                        .asOptional()
                        .build())
                .senderType(Player.class)
                .handler(AreaPriorityCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var plugin = JavaPlugin.getPlugin(Protect.class);
        var area = context.contains("area") ? Area.get(context.<String>get("area")) : Area.highestArea(player);
        if (area != null) {
            area.setPriority(context.<Integer>get("priority"));
            player.sendPlainMessage(Messages.AREA_PRIORITY_CHANGED.message(player.locale(), player,
                    Placeholder.of("area", area.getName()),
                    Placeholder.of("priority", area.getPriority())));
        } else player.sendPlainMessage(plugin.formatter().format(
                "%prefix% §c/area priority §8[§6priority§8] (§6area§8)"
        ));
    }
}
