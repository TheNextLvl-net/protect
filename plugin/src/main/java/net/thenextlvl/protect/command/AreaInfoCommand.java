package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.kyori.adventure.audience.Audience;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Locale;

class AreaInfoCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("info")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().map(Area::getDisplayName).filter(s ->
                                s.startsWith(token)).toList())
                        .asOptional().build())
                .handler(AreaInfoCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var area = context.contains("area") ? Area.get(context.<String>get("area")) :
                sender instanceof Entity entity ? Area.highestArea(entity) : null;
        var name = Placeholder.<Audience>of("area", () -> {
            if (area == null) return context.<String>get("area");
            return area.getName() + (area.isGlobalArea() ? " <dark_gray>(<gray>Global<dark_gray>)" : "");
        });
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        if (area != null) sendInfo(sender, area, name, locale);
        else sender.sendRichMessage(Messages.AREA_NOT_FOUND.message(locale, sender, name));
    }

    private static void sendInfo(CommandSender sender, Area area, Placeholder<Audience> name, Locale locale) {
        sender.sendRichMessage(Messages.AREA_INFO_NAME.message(locale, sender, name));
        if (!area.isGlobalArea()) {
            var world = Placeholder.<Audience>of("world", area.getWorld().getName());
            sender.sendRichMessage(Messages.AREA_INFO_WORLD.message(locale, sender, world));
        }
        var bounds = Placeholder.<Audience>of("bounds", () -> {
            if (area.isGlobalArea()) return "Infinite <dark_gray>(<gray>∞<dark_gray>)";
            return area.getRegion().getMinimumPoint().toParserString() + " - " +
                    area.getRegion().getMaximumPoint().toParserString();
        });
        sender.sendRichMessage(Messages.AREA_INFO_BOUNDS.message(locale, sender, bounds));
        var priority = Placeholder.<Audience>of("priority", area.getPriority());
        sender.sendRichMessage(Messages.AREA_INFO_PRIORITY.message(locale, sender, priority));
    }
}
