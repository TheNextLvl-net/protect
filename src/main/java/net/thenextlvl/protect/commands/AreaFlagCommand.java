package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.commands.argument.AreaArgumentType;
import net.thenextlvl.protect.commands.argument.FlagArgumentType;
import net.thenextlvl.protect.commands.argument.FlagProviderArgumentType;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class AreaFlagCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("flag")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag"))
                .then(info(plugin))
                .then(list(plugin))
                .then(set(plugin))
                .then(reset(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> info(final ProtectPlugin plugin) {
        return Commands.literal("info")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.info"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .executes(context -> {
                                    final var area = context.getArgument("area", Area.class);
                                    return info(context, area, plugin);
                                }))
                        .executes(context -> {
                            final var location = context.getSource().getLocation();
                            final var area = plugin.areaProvider().getArea(location);
                            return info(context, area, plugin);
                        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> list(final ProtectPlugin plugin) {
        return Commands.literal("list")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.list"))
                .then(Commands.argument("provider", new FlagProviderArgumentType(plugin))
                        .executes(context -> {
                            final var provider = context.getArgument("provider", Plugin.class);
                            return list(context, provider, plugin);
                        }))
                .executes(context -> {
                    plugin.flagRegistry().getRegistry().keySet()
                            .forEach(key -> list(context, key, plugin));
                    return Command.SINGLE_SUCCESS;
                });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(final ProtectPlugin plugin) {
        final var command = Commands.literal("set")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.set"));
        command.then(Commands.argument("flag", new FlagArgumentType(plugin))
            .then(Commands.argument("value", StringArgumentType.string())
                .suggests((context, builder) -> suggestValue(context, builder))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .executes(context -> {
                                    final var area = context.getArgument("area", Area.class);
                                    return set(context, area, plugin);
                                }))
                        .executes(context -> set(context, plugin.areaProvider().getArea(context.getSource().getLocation()), plugin))));
        return command;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(final ProtectPlugin plugin) {
        return Commands.literal("reset")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.reset"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin, flag ->
                                plugin.areaProvider().getAreas().anyMatch(area -> area.hasFlag(flag))))
                        .then(Commands.argument("area", new AreaArgumentType(plugin, (context, area) -> {
                            final var flag = (Flag<?>) context.getLastChild().getArgument("flag", Flag.class);
                            return area.hasFlag(flag);
                        })).executes(context -> {
                            final var area = context.getArgument("area", Area.class);
                            return reset(context, area, plugin);
                        })).executes(context -> {
                            final var location = context.getSource().getLocation();
                            return reset(context, plugin.areaProvider().getArea(location), plugin);
                        }));
    }

    private static int info(final CommandContext<CommandSourceStack> context, final Area area, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        plugin.bundle().sendMessage(sender, "area.flag.info",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(area.getFlag(flag))));
        return Command.SINGLE_SUCCESS;
    }

    private static int list(final CommandContext<CommandSourceStack> context, final Plugin provider, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var flags = plugin.flagRegistry().getFlags(provider);
        final var components = flags.stream()
                .map(flag -> plugin.bundle().component("area.flag.list.format", sender,
                        Placeholder.parsed("flag", flag.key().asString())))
                .toList();
        plugin.bundle().sendMessage(sender, "area.flag.list",
                Placeholder.parsed("provider", provider.getName()),
                Formatter.number("amount", flags.size()),
                Formatter.joining("flags", components));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int set(final CommandContext<CommandSourceStack> context, final Area area, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        final var value = resolve(flag, context.getArgument("value", String.class), area, plugin);
        final var message = area.setFlag((Flag) flag, value) ? "area.flag.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(value)));
        return Command.SINGLE_SUCCESS;
    }

    private static Object resolve(final Flag<?> flag, final String rawValue, final Area area, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var type = flag.type();
        if (type == Boolean.class) return BoolArgumentType.bool().parse(new StringReader(rawValue));
        if (type == Double.class) return DoubleArgumentType.doubleArg().parse(new StringReader(rawValue));
        if (type == Float.class) return FloatArgumentType.floatArg().parse(new StringReader(rawValue));
        if (type == Integer.class) return IntegerArgumentType.integer().parse(new StringReader(rawValue));
        if (type == Long.class) return LongArgumentType.longArg().parse(new StringReader(rawValue));
        if (type == String.class) return rawValue;
        if (type == Location.class) {
            try {
                final var split = rawValue.trim().split("\\s+");
                if (split.length != 3) throw new NumberFormatException("Expected 3 coordinates");
                return new Location(area.getWorld(), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            } catch (final NumberFormatException exception) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext(new StringReader(rawValue), rawValue);
            }
        }
        if (type == WeatherType.class) return WeatherType.valueOf(rawValue.toUpperCase());
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Unsupported flag type: " + type.getName());
    }

    private static CompletableFuture<Suggestions> suggestValue(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final var flag = context.getArgument("flag", Flag.class);
        final var remaining = builder.getRemaining().toLowerCase();

        if (flag.type() == Boolean.class) {
            suggest(builder, remaining, "true", "false");
        } else if (flag.type() == WeatherType.class) {
            for (final var weather : WeatherType.values()) {
                suggest(builder, remaining, weather.name().toLowerCase());
            }
        } else if (flag.type() == Location.class) {
            suggest(builder, remaining, "\"x y z\"");
        }

        return builder.buildFuture();
    }

    private static void suggest(final SuggestionsBuilder builder, final String remaining, final String... values) {
        for (final var value : values) {
            if (value.contains(remaining)) builder.suggest(value);
        }
    }

    private static int reset(final CommandContext<CommandSourceStack> context, final Area area, final ProtectPlugin plugin) {
        final var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        final var message = area.removeFlag(flag) ? "area.flag.reset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()));
        return Command.SINGLE_SUCCESS;
    }

}
