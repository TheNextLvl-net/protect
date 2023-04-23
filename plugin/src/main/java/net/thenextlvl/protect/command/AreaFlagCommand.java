package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class AreaFlagCommand {

    @Getter
    @RequiredArgsConstructor
    public enum Option {
        INFO("info", false), UNSET("unset", false), SET("set", true);

        private final String name;
        @Accessors(fluent = true)
        private final boolean requiresValues;

        @Nullable
        public static Option parse(String name) {
            for (Option option : values()) if (option.getName().equalsIgnoreCase(name)) return option;
            return null;
        }
    }

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("flag")
                .argument(StringArgument.<CommandSender>builder("option")
                        .withSuggestionsProvider((context, token) ->
                                Arrays.stream(Option.values()).map(Option::getName).toList())
                        .build())
                .argument(StringArgument.<CommandSender>builder("flag")
                        .withSuggestionsProvider((context, token) -> new ArrayList<>(Flag.names()))
                        .build())
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().map(Area::getDisplayName)
                                .filter(s -> s.startsWith(token)).toList())
                        .asOptional().build())
                .argument(StringArgument.<CommandSender>builder("value")
                        .withSuggestionsProvider((context, token) -> {
                            var option = Option.parse(context.get("option"));
                            if (option == null || !option.requiresValues()) return Collections.emptyList();
                            var flag = Flag.valueOf(context.get("flag"));
                            if (flag == null) return Collections.emptyList();
                            return flag.possibilities().keySet().stream().toList();
                        }).asOptional().build())
                .handler(AreaFlagCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var option = Option.parse(context.get("option"));
        var flag = Flag.valueOf(context.get("flag"));
        var area = context.contains("area") ? Area.get(context.<String>get("area")) :
                sender instanceof Entity entity ? Area.highestArea(entity) : null;
        if (option != null && option.equals(Option.INFO))
            handleInfo(sender, flag, area);
        else if (option != null && option.equals(Option.UNSET))
            handleUnset(sender, flag, area);
        else if (option != null && option.equals(Option.SET))
            handleSet(context, sender, flag, area);
        else if (option == null) sender.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                "%prefix% <red>/area flag <dark_gray>[<gold>option<dark_gray>] <dark_gray>[<gold>flag<dark_gray>] <dark_gray>[<gold>area<dark_gray>] <dark_gray>(<gold>value<dark_gray>)"
        ));
    }

    private static void handleSet(CommandContext<CommandSender> context, CommandSender sender, @Nullable Flag<Object> flag, @Nullable Area area) {
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        var value = flag != null && context.contains("value") ? flag.possibilities().get(context.<String>get("value")) : null;
        if (flag != null && value != null && area != null && area.setFlag(flag, value))
            sender.sendRichMessage(Messages.AREA_FLAG_CHANGED.message(locale, sender,
                    Placeholder.of("value", context.<String>get("value")),
                    Placeholder.of("area", area.getName()),
                    Placeholder.of("flag", flag.name())));
        else if (flag != null && value != null && area != null)
            sender.sendRichMessage(Messages.NOTHING_CHANGED.message(locale, sender));
        else sender.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                    "%prefix% <red>/area flag <dark_gray>[<gold>option<dark_gray>] <dark_gray>[<gold>flag<dark_gray>] <dark_gray>[<gold>area<dark_gray>] <dark_gray>(<gold>value<dark_gray>)"
            ));
    }

    private static void handleUnset(CommandSender sender, @Nullable Flag<Object> flag, @Nullable Area area) {
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        if (flag != null && area != null && area.unsetFlag(flag))
            sender.sendRichMessage(Messages.AREA_FLAG_UNSET.message(locale, sender,
                    Placeholder.of("area", area.getName()), Placeholder.of("flag", flag.name())));
        else if (flag != null && area != null)
            sender.sendRichMessage(Messages.NOTHING_CHANGED.message(locale, sender));
        else sender.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                    "%prefix% <red>/area flag <dark_gray>[<gold>option<dark_gray>] <dark_gray>[<gold>flag<dark_gray>] <dark_gray>(<gold>area<dark_gray>)"
            ));
    }

    private static void handleInfo(CommandSender sender, @Nullable Flag<Object> flag, @Nullable Area area) {
        var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
        if (flag != null && area != null) {
            sender.sendRichMessage(Messages.AREA_INFO_NAME.message(locale, sender,
                    Placeholder.of("area", area.getName() + (area.isGlobalArea() ? " <dark_gray>(<gray>Global<dark_gray>)" : ""))));
            sender.sendRichMessage(Messages.AREA_INFO_FLAG.message(locale, sender,
                    Placeholder.of("flag", flag.name())));
            if (!area.isDefault(flag)) sender.sendRichMessage(Messages.AREA_INFO_FLAG_DEFAULT.message(
                    locale, sender, Placeholder.of("flag", flag.possibilities().name(flag.defaultValue()))));
            sender.sendRichMessage(Messages.AREA_INFO_FLAG_VALUE.message(locale, sender,
                    Placeholder.of("flag", flag.possibilities().name(area.getFlag(flag)))));
        } else sender.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                "%prefix% <red>/area flag <dark_gray>[<gold>option<dark_gray>] <dark_gray>[<gold>flag<dark_gray>] <dark_gray>(<gold>area<dark_gray>)"
        ));
    }
}
