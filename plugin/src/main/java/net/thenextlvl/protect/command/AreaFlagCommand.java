package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;
import net.thenextlvl.protect.command.argument.EnumArgumentType;
import net.thenextlvl.protect.command.argument.FlagArgumentType;
import net.thenextlvl.protect.command.argument.FlagProviderArgumentType;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaFlagCommand {
    private final ProtectPlugin plugin;

    private final Map<Class<?>, Converter> argumentTypes = Map.of(
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

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("flag")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag"))
                .then(info())
                .then(list())
                .then(set())
                .then(reset());
    }

    private ArgumentBuilder<CommandSourceStack, ?> info() {
        return Commands.literal("info")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.info"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .executes(context -> {
                                    var area = context.getArgument("area", Area.class);
                                    return info(context, area);
                                }))
                        .executes(context -> {
                            var location = context.getSource().getLocation();
                            var area = plugin.areaProvider().getArea(location);
                            return info(context, area);
                        }));
    }

    private ArgumentBuilder<CommandSourceStack, ?> list() {
        return Commands.literal("list")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.list"))
                .then(Commands.argument("provider", new FlagProviderArgumentType(plugin))
                        .executes(context -> {
                            var provider = context.getArgument("provider", Plugin.class);
                            return list(context, provider);
                        }))
                .executes(context -> {
                    plugin.flagRegistry().getRegistry().keySet()
                            .forEach(key -> list(context, key));
                    return Command.SINGLE_SUCCESS;
                });
    }

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        var command = Commands.literal("set")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.set"));
        plugin.flagRegistry().getRegistry().values().forEach(flags -> flags.forEach(flag -> {
            var converter = argumentTypes.get(flag.type());
            if (converter == null) {
                plugin.getComponentLogger().error("No argument type for flag type: {}", flag.type().getName());
            } else command.then(Commands.literal(flag.key().asString())
                    .then(Commands.argument("value", converter.type().get())
                            .then(Commands.argument("area", new AreaArgumentType(plugin))
                                    .executes(context -> {
                                        var area = context.getArgument("area", Area.class);
                                        return set(context, converter.resolver(), flag, area);
                                    }))
                            .executes(context -> {
                                var location = context.getSource().getLocation();
                                return set(context, converter.resolver(), flag, plugin.areaProvider().getArea(location));
                            })));
        }));
        return command;
    }

    private ArgumentBuilder<CommandSourceStack, ?> reset() {
        return Commands.literal("reset")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.reset"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin, flag ->
                                plugin.areaProvider().getAreas().anyMatch(area -> area.hasFlag(flag))))
                        .then(Commands.argument("area", new AreaArgumentType(plugin, (context, area) -> {
                            var flag = (Flag<?>) context.getChild().getArgument("flag", Flag.class);
                            return area.hasFlag(flag);
                        })).executes(context -> {
                            var area = context.getArgument("area", Area.class);
                            return reset(context, area);
                        })).executes(context -> {
                            var location = context.getSource().getLocation();
                            return reset(context, plugin.areaProvider().getArea(location));
                        }));
    }

    private int info(CommandContext<CommandSourceStack> context, Area area) {
        var sender = context.getSource().getSender();
        var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        plugin.bundle().sendMessage(sender, "area.flag.info",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(area.getFlag(flag))));
        return Command.SINGLE_SUCCESS;
    }

    private int list(CommandContext<CommandSourceStack> context, Plugin provider) {
        var sender = context.getSource().getSender();
        var flags = plugin.flagRegistry().getFlags(provider);
        var components = flags.stream()
                .map(flag -> plugin.bundle().component(sender, "area.flag.list.format",
                        Placeholder.parsed("flag", flag.key().asString())))
                .toList();
        plugin.bundle().sendMessage(sender, "area.flag.list",
                Placeholder.parsed("provider", provider.getName()),
                Placeholder.parsed("amount", String.valueOf(flags.size())),
                Placeholder.component("flags", Component.join(JoinConfiguration.commas(true), components)));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int set(CommandContext<CommandSourceStack> context, Resolver<?> resolver, Flag flag, Area area) throws CommandSyntaxException {
        var value = resolver.resolve(flag.type(), context);
        var message = area.setFlag(flag, value) ? "area.flag.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(value)));
        return Command.SINGLE_SUCCESS;
    }

    private int reset(CommandContext<CommandSourceStack> context, Area area) {
        var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        var message = area.removeFlag(flag) ? "area.flag.reset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()));
        return Command.SINGLE_SUCCESS;
    }

    private record Converter(
            Supplier<ArgumentType<?>> type,
            Resolver<?> resolver
    ) {
        public Converter(Supplier<ArgumentType<?>> supplier) {
            this(supplier, (type, context) -> context.getArgument("value", type));
        }
    }

    @FunctionalInterface
    public interface Resolver<T> {
        T resolve(Class<T> type, CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }
}
