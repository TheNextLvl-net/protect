package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.commands.argument.AreaArgumentType;
import net.thenextlvl.protect.commands.argument.EnumArgumentType;
import net.thenextlvl.protect.commands.argument.FlagArgumentType;
import net.thenextlvl.protect.commands.argument.FlagProviderArgumentType;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.function.Supplier;

@NullMarked
final class AreaFlagCommand {
    private static final Map<Class<?>, Converter> argumentTypes = Map.of(
            Boolean.class, new Converter(BoolArgumentType::bool),
            Double.class, new Converter(DoubleArgumentType::doubleArg),
            Float.class, new Converter(FloatArgumentType::floatArg),
            Integer.class, new Converter(IntegerArgumentType::integer),
            Long.class, new Converter(LongArgumentType::longArg),
            String.class, new Converter(StringArgumentType::string),
            Location.class, new Converter(ArgumentTypes::finePosition, (type, context) -> {
                var resolver = context.getArgument("value", FinePositionResolver.class);
                var area = context.getArgument("area", Area.class);
                return resolver.resolve(context.getSource()).toLocation(area.getWorld());
            }),
            WeatherType.class, new Converter(() -> new EnumArgumentType<>(WeatherType.class))
    );

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
        plugin.flagRegistry().getRegistry().values().forEach(flags -> flags.forEach(flag -> {
            final var converter = argumentTypes.get(flag.type());
            if (converter == null) {
                plugin.getComponentLogger().error("No argument type for flag type: {}", flag.type().getName());
            } else command.then(Commands.literal(flag.key().asString())
                    .then(Commands.argument("value", converter.type().get())
                            .then(Commands.argument("area", new AreaArgumentType(plugin))
                                    .executes(context -> {
                                        final var area = context.getArgument("area", Area.class);
                                        return set(context, converter.resolver(), flag, area, plugin);
                                    }))
                            .executes(context -> {
                                final var location = context.getSource().getLocation();
                                return set(context, converter.resolver(), flag, plugin.areaProvider().getArea(location), plugin);
                            })));
        }));
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
    private static int set(final CommandContext<CommandSourceStack> context, final Resolver<?> resolver, final Flag flag, final Area area, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var value = resolver.resolve(flag.type(), context);
        final var message = area.setFlag(flag, value) ? "area.flag.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(value)));
        return Command.SINGLE_SUCCESS;
    }

    private static int reset(final CommandContext<CommandSourceStack> context, final Area area, final ProtectPlugin plugin) {
        final var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        final var message = area.removeFlag(flag) ? "area.flag.reset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()));
        return Command.SINGLE_SUCCESS;
    }

    private record Converter(
            Supplier<ArgumentType<?>> type,
            Resolver<?> resolver
    ) {
        public Converter(final Supplier<ArgumentType<?>> supplier) {
            this(supplier, (type, context) -> context.getArgument("value", type));
        }
    }

    @FunctionalInterface
    public interface Resolver<T> {
        T resolve(Class<T> type, CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }
}
