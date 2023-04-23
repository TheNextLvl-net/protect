package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class AreaListCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("list").handler(AreaListCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var global = Area.areas().filter(Area::isGlobalArea).map(Area::getName).toList();
        var user = Area.areas().filter(area -> !area.isGlobalArea()).map(Area::getName).toList();
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        sender.sendRichMessage(Messages.AREA_LIST.message(locale, sender,
                Placeholder.of("amount", user.size()), Placeholder.of("areas", String.join(", ", user))));
        sender.sendRichMessage(Messages.AREA_LIST_GLOBAL.message(locale, sender,
                Placeholder.of("amount", global.size()), Placeholder.of("areas", String.join(", ", global))));
    }
}
