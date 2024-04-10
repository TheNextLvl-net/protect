package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
class AreaFlagCommand {
    private final ProtectPlugin plugin;

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

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("flag")
                .argument(StringArgument.<CommandSender>builder("option")
                        .withSuggestionsProvider((context, token) ->
                                Arrays.stream(Option.values()).map(Option::getName).toList())
                        .build())
                .argument(StringArgument.<CommandSender>builder("flag")
                        .withSuggestionsProvider((context, token) -> plugin.flagRegistry().getFlags().stream()
                                .map(flag -> flag.key().asString())
                                .toList())
                        .build())
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> plugin.areaProvider().getAreas()
                                .map(Area::getName)
                                .filter(s -> s.startsWith(token))
                                .toList())
                        .asOptional().build())
                .argument(StringArgument.<CommandSender>builder("value")
                        .withSuggestionsProvider((context, token) -> {
                            var option = Option.parse(context.get("option"));
                            if (option == null || !option.requiresValues()) return Collections.emptyList();
                            var key = NamespacedKey.fromString(context.get("flag"));
                            if (key == null) return Collections.emptyList();
                            var flag = plugin.flagRegistry().getFlag(key).orElse(null);
                            if (flag == null) return Collections.emptyList();
                            if (flag.type().equals(Boolean.class)) return List.of("true", "false");
                            else if (flag.type().equals(Integer.class))
                                return IntStream.rangeClosed(0, 100).mapToObj(Integer::toString).toList();
                            else if (flag.type().equals(Double.class))
                                return IntStream.rangeClosed(0, 100).mapToObj(Double::toString).toList();
                            else if (flag.type().equals(Float.class))
                                return IntStream.rangeClosed(0, 100).mapToObj(Float::toString).toList();
                            else if (flag.type().equals(Long.class))
                                return IntStream.rangeClosed(0, 100).mapToObj(Long::toString).toList();
                            else if (flag.type().isEnum())
                                return Arrays.stream(flag.type().getEnumConstants()).map(String::valueOf).toList();
                            return Collections.emptyList();
                        }).greedy().asOptional().build())
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var option = Option.parse(context.get("option"));
        var key = NamespacedKey.fromString(context.get("flag"));
        var flag = key != null ? plugin.flagRegistry().getFlag(key).orElse(null) : null;
        var name = context.contains("area") ? context.<String>get("area") : null;
        // todo pattern validation
        var area = name != null ? plugin.areaProvider().getArea(name).orElse(null) :
                sender instanceof Entity entity ? plugin.areaProvider().getArea(entity) : null;
        if (option == null) plugin.bundle().sendMessage(sender, "command.area.flag");
        else switch (option) {
            case SET -> handleSet(context, sender, flag, area);
            case UNSET -> handleUnset(sender, flag, area);
            case INFO -> handleInfo(sender, flag, area);
        }
    }

    private void handleSet(CommandContext<CommandSender> context, CommandSender sender, @Nullable Flag<Object> flag, @Nullable Area area) {
        var value = context.contains("value") ? context.<String>get("value") : null;
        if (flag == null || area == null || value == null) plugin.bundle().sendMessage(sender, "command.area.flag");
        else try {
            if (setFlagValue(flag, area, value)) plugin.bundle().sendMessage(sender, "area.flag.changed",
                    Placeholder.parsed("value", value),
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("flag", flag.key().asString()));
            else plugin.bundle().sendMessage(sender, "nothing.changed");
        } catch (Exception e) {
            plugin.bundle().sendMessage(sender, "command.area.flag");
        }
    }

    private boolean setFlagValue(@NotNull Flag<Object> flag, @NotNull Area area, @NotNull String value) {
        if (flag.type().equals(Boolean.class)) {
            var state = Boolean.parseBoolean(value);
            if (!Objects.equals(area.getFlag(flag), state)) area.setFlag(flag, state);
            else return false;
        } else if (flag.type().equals(Integer.class)) {
            var state = Integer.parseInt(value);
            if (!Objects.equals(area.getFlag(flag), state)) area.setFlag(flag, state);
            else return false;
        } else if (flag.type().equals(Double.class)) {
            var state = Double.parseDouble(value);
            if (!Objects.equals(area.getFlag(flag), state)) area.setFlag(flag, state);
            else return false;
        } else if (flag.type().equals(Float.class)) {
            var state = Float.parseFloat(value);
            if (!Objects.equals(area.getFlag(flag), state)) area.setFlag(flag, state);
            else return false;
        } else if (flag.type().equals(Long.class)) {
            var state = Long.parseLong(value);
            if (!Objects.equals(state, area.getFlag(flag))) area.setFlag(flag, state);
            else return false;
        } else if (flag.type().equals(String.class)) {
            if (!Objects.equals(area.getFlag(flag), value)) area.setFlag(flag, value);
            else return false;
        } else if (flag.type().isEnum()) {
            var state = Arrays.stream(flag.type().getEnumConstants())
                    .filter(o -> String.valueOf(o).equalsIgnoreCase(value))
                    .findAny().orElseThrow(() -> new IllegalStateException("No enum constant " + value));
            if (!Objects.equals(area.getFlag(flag), state)) area.setFlag(flag, state);
        } else return false;
        return true;
    }

    private void handleUnset(CommandSender sender, @Nullable Flag<Object> flag, @Nullable Area area) {
        if (flag != null && area != null && area.removeFlag(flag))
            plugin.bundle().sendMessage(sender, "area.flag.unset",
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("flag", flag.key().asString()));
        else if (flag != null && area != null) plugin.bundle().sendMessage(sender, "nothing.changed");
        else plugin.bundle().sendMessage(sender, "command.area.flag");
    }

    private void handleInfo(CommandSender sender, @Nullable Flag<Object> flag, @Nullable Area area) {
        if (flag == null || area == null) {
            plugin.bundle().sendMessage(sender, "command.area.flag");
            return;
        }
        plugin.bundle().sendMessage(sender, "area.info.name", Placeholder.parsed("area", area.getName()));
        plugin.bundle().sendMessage(sender, "area.info.flag", Placeholder.parsed("flag", flag.key().asString()));
        if (area.hasFlag(flag)) plugin.bundle().sendMessage(sender, "area.info.flag.default",
                Placeholder.parsed("flag", String.valueOf(flag.defaultValue())));
        plugin.bundle().sendMessage(sender, "area.info.flag.value",
                Placeholder.parsed("flag", String.valueOf(area.getFlag(flag))));
    }
}
