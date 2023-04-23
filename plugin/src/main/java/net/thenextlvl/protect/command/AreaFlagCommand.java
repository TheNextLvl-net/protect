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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nullable;
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
                .senderType(Player.class)
                .handler(AreaFlagCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var option = Option.parse(context.get("option"));
        var flag = Flag.valueOf(context.get("flag"));
        var plugin = JavaPlugin.getPlugin(Protect.class);
        var area = context.contains("area") ? Area.get(context.<String>get("area")) : Area.highestArea(player.getLocation());
        if (option != null && option.equals(Option.INFO)) {
            if (flag != null && area != null) {
                player.sendRichMessage(Messages.AREA_INFO_NAME.message(player.locale(),
                        Placeholder.of("area", area.getName() + (area.isGlobalArea() ? " §8(§7Global§8)" : ""))));
                player.sendRichMessage(Messages.AREA_INFO_FLAG.message(player.locale(),
                        Placeholder.of("flag", flag.name())));
                if (!area.isDefault(flag)) player.sendRichMessage(Messages.AREA_INFO_FLAG_DEFAULT.message(
                        player.locale(), Placeholder.of("flag", flag.possibilities().name(flag.defaultValue()))));
                player.sendRichMessage(Messages.AREA_INFO_FLAG_VALUE.message(player.locale(),
                        Placeholder.of("flag", flag.possibilities().name(area.getFlag(flag)))));
            } else player.sendRichMessage(plugin.formatter().format(
                    "%prefix% §c/area flag §8[§6option§8] §8[§6flag§8] §8(§6area§8)"
            ));
        } else if (option != null && option.equals(Option.UNSET)) {
            if (flag != null && area != null && area.unsetFlag(flag))
                player.sendRichMessage(Messages.AREA_FLAG_UNSET.message(player.locale(), player,
                        Placeholder.of("area", area.getName()), Placeholder.of("flag", flag.name())));
            else if (flag != null && area != null)
                player.sendRichMessage(Messages.NOTHING_CHANGED.message(player.locale(), player));
            else player.sendRichMessage(plugin.formatter().format(
                        "%prefix% §c/area flag §8[§6option§8] §8[§6flag§8] §8(§6area§8)"
                ));
        } else if (option != null && option.equals(Option.SET)) {
            var value = flag != null && context.contains("value") ? flag.possibilities().get(context.<String>get("value")) : null;
            if (flag != null && value != null && area != null && area.setFlag(flag, value))
                player.sendRichMessage(Messages.AREA_FLAG_CHANGED.message(player.locale(), player,
                        Placeholder.of("value", context.<String>get("value")),
                        Placeholder.of("area", area.getName()),
                        Placeholder.of("flag", flag.name())));
            else if (flag != null && value != null && area != null)
                player.sendRichMessage(Messages.NOTHING_CHANGED.message(player.locale(), player));
            else player.sendRichMessage(plugin.formatter().format(
                        "%prefix% §c/area flag §8[§6option§8] §8[§6flag§8] §8[§6area§8] §8(§6value§8)"
                ));
        } else if (option == null) player.sendRichMessage(plugin.formatter().format(
                "%prefix% §c/area flag §8[§6option§8] §8[§6flag§8] §8[§6area§8] §8(§6value§8)"
        ));
    }
}
