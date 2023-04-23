package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.kyori.adventure.audience.Audience;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class AreaDeleteCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("delete")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().filter(area ->
                                !area.isGlobalArea()).map(Area::getName).filter(s ->
                                s.startsWith(token)).toList())
                        .build())
                .handler(AreaDeleteCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var name = context.<String>get("area");
        var area = Area.get(name);
        var sender = context.getSender();
        var placeholder = Placeholder.<Audience>of("area", name);
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        if (area != null && area.delete()) {
            sender.sendRichMessage(Messages.AREA_DELETE_SUCCEEDED.message(locale, sender, placeholder));
        } else if (area != null) {
            sender.sendRichMessage(Messages.AREA_DELETE_FAILED.message(locale, sender, placeholder));
        } else sender.sendRichMessage(Messages.AREA_NOT_FOUND.message(locale, sender, placeholder));
    }
}
