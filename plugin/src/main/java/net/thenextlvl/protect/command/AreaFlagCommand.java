package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
abstract class AreaFlagCommand {
    protected final ProtectPlugin plugin;
    protected final Command.Builder<CommandSourceStack> builder;

    protected final Command.Builder<CommandSourceStack> flagCommand() {
        return builder.literal("flag")
                .commandDescription(Description.description("query, change or reset flags"));
    }

    abstract Command.Builder<CommandSourceStack> create();

    static class Info extends AreaFlagCommand {
        public Info(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return flagCommand().literal("info")
                    .permission("protect.command.area.flag.info")
                    .commandDescription(Description.description("query information about area flags"))
                    .required("flag",
                            NamespacedKeyParser.namespacedKeyParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getFlags().stream()
                                    .map(flag -> flag.key().asString())
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .optional("area",
                            StringParser.greedyStringParser(),
                            DefaultValue.dynamic(context -> {
                                if (!(context.sender().getSender() instanceof Player player)) return "";
                                return plugin.areaProvider().getArea(player).getName();
                            }),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .map(Area::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSourceStack> context, Flag<Object> flag, Area area) {
            var sender = context.sender().getSender();
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

    static class List extends AreaFlagCommand {
        public List(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return flagCommand().literal("list")
                    .permission("protect.command.area.flag.list")
                    .commandDescription(Description.description("list all existing area flags"))
                    .optional("plugin", StringParser.greedyStringParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getRegistry()
                                    .keySet().stream()
                                    .map(Plugin::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSourceStack> context) {
            context.<String>optional("plugin")
                    .map(name -> Bukkit.getPluginManager().getPlugin(name))
                    .ifPresentOrElse(plugin -> sendFlags(plugin, context.sender().getSender()), () ->
                            this.plugin.flagRegistry().getRegistry().keySet()
                                    .forEach(plugin -> sendFlags(plugin, context.sender().getSender())));
        }

        private void sendFlags(Plugin plugin, CommandSender sender) {
            var flags = this.plugin.flagRegistry().getFlags(plugin);
            var components = flags.stream()
                    .map(flag -> Component.text(flag.key().getKey())
                            .hoverEvent(HoverEvent.showText(this.plugin.bundle().component(sender, "area.flag.list.hover")))
                            .clickEvent(ClickEvent.suggestCommand("/area flag set " + flag.key().asString() + " ")))
                    .collect(Collectors.toSet());
            this.plugin.bundle().sendMessage(sender, "area.flag.list",
                    Placeholder.parsed("plugin", plugin.getName()),
                    Placeholder.parsed("amount", String.valueOf(flags.size())),
                    Placeholder.component("flags", Component.join(JoinConfiguration.commas(true), components)));
        }

        @Override
        protected String usage() {
            return "area flag list";
        }
    }

    static class Set extends AreaFlagCommand {
        public Set(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return flagCommand().literal("set")
                    .permission("protect.command.area.flag.set")
                    .commandDescription(Description.description("change specified flags of areas"))
                    .required("flag",
                            NamespacedKeyParser.namespacedKeyParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getFlags().stream()
                                    .map(flag -> flag.key().asString())
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .required("value",
                            StringParser.greedyStringParser(),
                            SuggestionProvider.blocking((context, input) -> {
                                var key = context.<NamespacedKey>get("flag");
                                var flag = plugin.flagRegistry().getFlag(key).orElse(null);
                                if (flag == null) return java.util.List.of();
                                if (flag.type().equals(Boolean.class)) return Stream.of("true", "false")
                                        .map(Suggestion::suggestion).toList();
                                else if (flag.type().equals(Integer.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Integer::toString)
                                            .map(Suggestion::suggestion).toList();
                                else if (flag.type().equals(Double.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Double::toString)
                                            .map(Suggestion::suggestion).toList();
                                else if (flag.type().equals(Float.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Float::toString)
                                            .map(Suggestion::suggestion).toList();
                                else if (flag.type().equals(Long.class))
                                    return IntStream.rangeClosed(0, 100).mapToObj(Long::toString)
                                            .map(Suggestion::suggestion).toList();
                                else if (flag.type().isEnum())
                                    return Arrays.stream(flag.type().getEnumConstants()).map(String::valueOf)
                                            .map(Suggestion::suggestion).toList();
                                return java.util.List.of();
                            }))
                    .optional("area",
                            StringParser.greedyStringParser(),
                            DefaultValue.dynamic(context -> {
                                if (!(context.sender().getSender() instanceof Player player)) return "";
                                return plugin.areaProvider().getArea(player).getName();
                            }),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .map(Area::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSourceStack> context, Flag<Object> flag, Area area) {
            var value = context.<String>get("value");
            var sender = context.sender().getSender();
            try {
                if (setFlagValue(flag, area, value)) plugin.bundle().sendMessage(sender, "area.flag.changed",
                        Placeholder.parsed("value", value),
                        Placeholder.parsed("area", area.getName()),
                        Placeholder.parsed("flag", flag.key().asString()));
                else plugin.bundle().sendMessage(sender, "nothing.changed");
            } catch (Exception e) {
                plugin.bundle().sendMessage(sender, "area.flag.value",
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
        public Unset(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return flagCommand().literal("unset")
                    .permission("protect.command.area.flag.unset")
                    .commandDescription(Description.description("reset specified flags of areas"))
                    .required("flag",
                            NamespacedKeyParser.namespacedKeyParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.flagRegistry().getFlags().stream()
                                    .map(flag -> flag.key().asString())
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .optional("area",
                            StringParser.greedyStringParser(),
                            DefaultValue.dynamic(context -> {
                                if (!(context.sender().getSender() instanceof Player player)) return "";
                                return plugin.areaProvider().getArea(player).getName();
                            }),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .map(Area::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandContext<CommandSourceStack> context, Flag<Object> flag, Area area) {
            if (area.removeFlag(flag)) plugin.bundle().sendMessage(context.sender().getSender(), "area.flag.unset",
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("flag", flag.key().asString()));
            else plugin.bundle().sendMessage(context.sender().getSender(), "nothing.changed");
        }

        @Override
        protected String usage() {
            return "area flag unset [flag] (area)";
        }
    }

    protected void execute(CommandContext<CommandSourceStack> context) {
        var sender = context.sender();
        var key = context.<NamespacedKey>get("flag");
        var flag = plugin.flagRegistry().getFlag(key).orElse(null);
        var name = context.contains("area") ? context.<String>get("area") : null;
        var area = name != null ? plugin.areaProvider().getArea(name).orElse(null) :
                sender instanceof Entity entity ? plugin.areaProvider().getArea(entity) : null;
        if (flag != null && area != null) execute(context, flag, area);
        else throw new InvalidSyntaxException(usage(), sender, java.util.List.of());
    }

    protected void execute(CommandContext<CommandSourceStack> context, Flag<Object> flag, Area area) {
    }

    protected abstract String usage();
}
