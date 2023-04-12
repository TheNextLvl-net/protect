package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class AreaInfoCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("info")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().map(Area::getDisplayName).filter(s ->
                                s.startsWith(token)).toList())
                        .asOptional().build())
                .senderType(Player.class)
                .handler(AreaInfoCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var area = context.contains("area") ? Area.get(context.<String>get("area")) : Area.highestArea(player);
        var name = Placeholder.<Player>of("area", () -> {
            if (area == null) return context.<String>get("area");
            return area.getName() + (area.isGlobalArea() ? " §8(§7Global§8)" : "");
        });
        if (area != null) {
            player.sendRichMessage(Messages.AREA_INFO_NAME.message(player.locale(), player, name));
            if (!area.isGlobalArea()) {
                var world = Placeholder.<Player>of("world", area.getWorld().getName());
                player.sendRichMessage(Messages.AREA_INFO_WORLD.message(player.locale(), player, world));
            }
            var bounds = Placeholder.<Player>of("bounds", () -> {
                if (area.isGlobalArea()) return "Infinite §8(§7∞§8)";
                return area.getRegion().getMinimumPoint().toParserString() + " - " +
                        area.getRegion().getMaximumPoint().toParserString();
            });
            player.sendRichMessage(Messages.AREA_INFO_BOUNDS.message(player.locale(), player, bounds));
            var priority = Placeholder.<Player>of("priority", area.getPriority());
            player.sendRichMessage(Messages.AREA_INFO_PRIORITY.message(player.locale(), player, priority));
        } else player.sendRichMessage(Messages.AREA_NOT_FOUND.message(player.locale(), player, name));
    }
}
