package net.thenextlvl.protect.command;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.parser.NamespacedKeyParser;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
abstract class AreaFlagCommand {
    protected final ProtectPlugin plugin;
    protected final Command.Builder<CommandSender> builder;

    protected final Command.Builder<CommandSender> flagCommand() {
        return builder.literal("flag")
                .commandDescription(Description.description("query, change or reset flags"));
    }

    abstract Command.Builder<CommandSender> create();

    static class Info extends AreaFlagCommand {
        public Info(ProtectPlugin plugin, Command.Builder<CommandSender> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSender> create() {
            return flagCommand().literal("info")
                    .permission("protect.command.area.flag.info")
                    .commandDescription(Description.description("query information about area flags"))
                    .required("flag",
                            NamespacedKeyParser.namespacedKeyParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getFlags().stream()
                                    .map(flag -> flag.key().asString())
                                    .map(Suggestion::simple)
                                    .toList()))
                    .optional("area",
                            StringParser.greedyStringParser(),
                            DefaultValue.dynamic(context -> {
                                if (!(context.sender() instanceof Player player)) return "";
                                return plugin.areaProvider().getArea(player).getName();
                            }),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .map(Area::getName)
                                    .map(Suggestion::simple)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSender> context, Flag<Object> flag, Area area) {
            var sender = context.sender();
            plugin.bundle().sendMessage(sender, "area.info.name", Placeholder.parsed("area", area.getName()));
            plugin.bundle().sendMessage(sender, "area.info.flag", Placeholder.parsed("flag", flag.key().asString()));
            if (area.hasFlag(flag)) plugin.bundle().sendMessage(sender, "area.info.flag.default",
                    Placeholder.parsed("flag", String.valueOf(flag.defaultValue())));
            plugin.bundle().sendMessage(sender, "area.info.flag.value",
                    Placeholder.parsed("flag", String.valueOf(area.getFlag(flag))));
        }

        @Override
        protected String usage() {
            return "area flag info [flag] (area)";
        }
    }

    static class Set extends AreaFlagCommand {
        public Set(ProtectPlugin plugin, Command.Builder<CommandSender> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSender> create() {
            return flagCommand().literal("set")
                    .permission("protect.command.area.flag.set")
                    .commandDescription(Description.description("change specified flags of areas"))
                    .required("flag",
                            NamespacedKeyParser.namespacedKeyParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getFlags().stream()
                                    .map(flag -> flag.key().asString())
                                    .map(Suggestion::simple)
                                    .toList()))
                    .required("value",
                            StringParser.greedyStringParser(),
                            SuggestionProvider.blocking((context, input) -> {
                                var key = context.<NamespacedKey>get("flag");
                                var flag = plugin.flagRegistry().getFlag(key).orElse(null);
                                if (flag == null) return List.of();
                                if (flag.type().equals(Boolean.class)) return Stream.of("true", "false")
                                        .map(Suggestion::simple).toList();
                                else if (flag.type().equals(Integer.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Integer::toString)
                                            .map(Suggestion::simple).toList();
                                else if (flag.type().equals(Double.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Double::toString)
                                            .map(Suggestion::simple).toList();
                                else if (flag.type().equals(Float.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Float::toString)
                                            .map(Suggestion::simple).toList();
                                else if (flag.type().equals(Long.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Long::toString)
                                            .map(Suggestion::simple).toList();
                                else if (flag.type().isEnum())
                                    return Arrays.stream(flag.type().getEnumConstants()).map(String::valueOf)
                                            .map(Suggestion::simple).toList();
                                return List.of();
                            }))
                    .optional("area",
                            StringParser.greedyStringParser(),
                            DefaultValue.dynamic(context -> {
                                if (!(context.sender() instanceof Player player)) return "";
                                return plugin.areaProvider().getArea(player).getName();
                            }),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .map(Area::getName)
                                    .map(Suggestion::simple)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSender> context, Flag<Object> flag, Area area) {
            var value = context.<String>get("value");
            try {
                if (setFlagValue(flag, area, value)) plugin.bundle().sendMessage(context.sender(), "area.flag.changed",
                        Placeholder.parsed("value", value),
                        Placeholder.parsed("area", area.getName()),
                        Placeholder.parsed("flag", flag.key().asString()));
                else plugin.bundle().sendMessage(context.sender(), "nothing.changed");
            } catch (Exception e) {
                plugin.bundle().sendMessage(context.sender(), "area.flag.value",
                        Placeholder.parsed("flag", flag.key().asString()),
                        Placeholder.parsed("value", value));
            }
        }

        @Override
        protected String usage() {
            return "area flag set [flag] [value] (area)";
        }

        private boolean setFlagValue(@NotNull Flag<Object> flag, @NotNull Area area, @NotNull String value) {
            if (flag.type().equals(Boolean.class)) {
                return area.setFlag(flag, Boolean.parseBoolean(value));
            } else if (flag.type().equals(Integer.class)) {
                return area.setFlag(flag, Integer.parseInt(value));
            } else if (flag.type().equals(Double.class)) {
                return area.setFlag(flag, Double.parseDouble(value));
            } else if (flag.type().equals(Float.class)) {
                return area.setFlag(flag, Float.parseFloat(value));
            } else if (flag.type().equals(Long.class)) {
                return area.setFlag(flag, Long.parseLong(value));
            } else if (flag.type().equals(String.class)) {
                return area.setFlag(flag, value);
            } else if (flag.type().isEnum()) {
                var state = Arrays.stream(flag.type().getEnumConstants())
                        .filter(o -> String.valueOf(o).equalsIgnoreCase(value))
                        .findAny().orElseThrow(() -> new IllegalStateException("No enum constant " + value));
                return area.setFlag(flag, state);
            } else {
                plugin.getComponentLogger().error("Unknown flag type: {}", flag.type().getName());
                return false;
            }
        }
    }

    static class Unset extends AreaFlagCommand {
        public Unset(ProtectPlugin plugin, Command.Builder<CommandSender> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSender> create() {
            return flagCommand().literal("unset")
                    .permission("protect.command.area.flag.unset")
                    .commandDescription(Description.description("reset specified flags of areas"))
                    .required("flag",
                            NamespacedKeyParser.namespacedKeyParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getFlags().stream()
                                    .map(flag -> flag.key().asString())
                                    .map(Suggestion::simple)
                                    .toList()))
                    .optional("area",
                            StringParser.greedyStringParser(),
                            DefaultValue.dynamic(context -> {
                                if (!(context.sender() instanceof Player player)) return "";
                                return plugin.areaProvider().getArea(player).getName();
                            }),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .map(Area::getName)
                                    .map(Suggestion::simple)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSender> context, Flag<Object> flag, Area area) {
            if (area.removeFlag(flag)) plugin.bundle().sendMessage(context.sender(), "area.flag.unset",
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("flag", flag.key().asString()));
            else plugin.bundle().sendMessage(context.sender(), "nothing.changed");
        }

        @Override
        protected String usage() {
            return "area flag unset [flag] (area)";
        }
    }

    protected final void execute(CommandContext<CommandSender> context) {
        var sender = context.sender();
        var key = context.<NamespacedKey>get("flag");
        var flag = plugin.flagRegistry().getFlag(key).orElse(null);
        var name = context.contains("area") ? context.<String>get("area") : null;
        var area = name != null ? plugin.areaProvider().getArea(name).orElse(null) :
                sender instanceof Entity entity ? plugin.areaProvider().getArea(entity) : null;
        if (flag != null && area != null) execute(context, flag, area);
        else throw new InvalidSyntaxException(usage(), sender, List.of());
    }

    protected abstract void execute(CommandContext<CommandSender> context, Flag<Object> flag, Area area);

    protected abstract String usage();
}
