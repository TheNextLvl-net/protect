package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;
import net.thenextlvl.protect.command.argument.EnumArgumentType;
import net.thenextlvl.protect.command.argument.FlagArgumentType;
import net.thenextlvl.protect.command.argument.FlagProviderArgumentType;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.WeatherType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaFlagCommand {
    private final ProtectPlugin plugin;

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

    private final Map<Class<?>, Supplier<ArgumentType<?>>> argumentTypes = Map.of(
            Boolean.class, BoolArgumentType::bool,
            Double.class, DoubleArgumentType::doubleArg,
            Float.class, FloatArgumentType::floatArg,
            Integer.class, IntegerArgumentType::integer,
            Long.class, LongArgumentType::longArg,
            String.class, StringArgumentType::string,
            WeatherType.class, () -> new EnumArgumentType<>(WeatherType.class)
    );

    private ArgumentBuilder<CommandSourceStack, ?> set() {
        var command = Commands.literal("set")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.set"));
        plugin.flagRegistry().getRegistry().values().forEach(flags -> flags.forEach(flag -> {
            var supplier = argumentTypes.get(flag.type());
            if (supplier == null) {
                plugin.getComponentLogger().error("No argument type for flag type: {}", flag.type().getName());
            } else command.then(Commands.literal(flag.key().asString())
                    .then(Commands.argument("value", supplier.get())
                            .then(Commands.argument("area", new AreaArgumentType(plugin))
                                    .executes(context -> {
                                        var area = context.getArgument("area", Area.class);
                                        return set(context, flag, area);
                                    }))
                            .executes(context -> {
                                var location = context.getSource().getLocation();
                                return set(context, flag, plugin.areaProvider().getArea(location));
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
        plugin.bundle().sendMessage(sender, "area.info.name", Placeholder.parsed("area", area.getName()));
        plugin.bundle().sendMessage(sender, "area.info.flag", Placeholder.parsed("flag", flag.key().asString()));
        if (area.hasFlag(flag)) plugin.bundle().sendMessage(sender, "area.info.flag.default",
                Placeholder.parsed("flag", String.valueOf(flag.defaultValue())));
        plugin.bundle().sendMessage(sender, "area.info.flag.value",
                Placeholder.parsed("flag", String.valueOf(area.getFlag(flag))));
        return Command.SINGLE_SUCCESS;
    }

    private int list(CommandContext<CommandSourceStack> context, Plugin provider) {
        var sender = context.getSource().getSender();
        var flags = plugin.flagRegistry().getFlags(provider);
        var components = flags.stream()
                .map(flag -> Component.text(flag.key().getKey())
                        .hoverEvent(HoverEvent.showText(plugin.bundle().component(sender, "area.flag.list.hover")))
                        .clickEvent(ClickEvent.suggestCommand("/area flag set " + flag.key().asString() + " ")))
                .collect(Collectors.toSet());
        plugin.bundle().sendMessage(sender, "area.flag.list",
                Placeholder.parsed("provider", provider.getName()),
                Placeholder.parsed("amount", String.valueOf(flags.size())),
                Placeholder.component("flags", Component.join(JoinConfiguration.commas(true), components)));
        return Command.SINGLE_SUCCESS;
    }

    private <T> int set(CommandContext<CommandSourceStack> context, Flag<T> flag, Area area) {
        var value = context.getArgument("value", flag.type());
        var message = area.setFlag(flag, value) ? "area.flag.changed" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.parsed("value", String.valueOf(value)));
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
}
