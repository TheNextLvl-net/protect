package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.api.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

class AreaDeleteCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("delete")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().filter(area ->
                                !area.isGlobalArea()).map(Area::getName).filter(s ->
                                s.startsWith(token)).toList())
                        .build())
                .senderType(Player.class)
                .handler(AreaDeleteCommand::execute);
    }

    private static void execute(@NonNull CommandContext<CommandSender> context) {
        var name = context.<String>get("area");
        var area = Area.get(name);
        var player = (Player) context.getSender();
        var placeholder = Placeholder.<Player>of("area", name);
        if (area != null && area.delete()) {
            player.sendRichMessage(Messages.AREA_DELETE_SUCCEEDED.message(player.locale(), player, placeholder));
        } else if (area != null) {
            player.sendRichMessage(Messages.AREA_DELETE_FAILED.message(player.locale(), player, placeholder));
        } else player.sendRichMessage(Messages.AREA_NOT_FOUND.message(player.locale(), player, placeholder));
    }
}
