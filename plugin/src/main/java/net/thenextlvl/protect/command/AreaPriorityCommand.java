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
import org.bukkit.entity.Entity;
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
                .handler(AreaPriorityCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var area = context.contains("area") ? Area.get(context.<String>get("area")) :
                sender instanceof Entity entity ? Area.highestArea(entity) : null;
        if (area != null) setPriority(context, sender, area);
        else sender.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                "%prefix% <red>/area priority <dark_gray>[<gold>priority<dark_gray>] (<gold>area<dark_gray>)"
        ));
    }

    private static void setPriority(CommandContext<CommandSender> context, CommandSender sender, Area area) {
        area.setPriority(context.<Integer>get("priority"));
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        sender.sendRichMessage(Messages.AREA_PRIORITY_CHANGED.message(locale, sender,
                Placeholder.of("area", area.getName()),
                Placeholder.of("priority", area.getPriority())));
    }
}
