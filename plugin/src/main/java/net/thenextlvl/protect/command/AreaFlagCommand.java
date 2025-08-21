package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;
import net.thenextlvl.protect.command.argument.FlagArgumentType;
import net.thenextlvl.protect.command.argument.FlagProviderArgumentType;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.plugin.Plugin;

class AreaFlagCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("flag")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag"))
                .then(info(plugin))
                .then(list(plugin))
                .then(set(plugin))
                .then(reset(plugin));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> info(ProtectPlugin plugin) {
        return Commands.literal("info")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.info"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .executes(context -> {
                                    var area = context.getArgument("area", Area.class);
                                    return info(context, area, plugin);
                                }))
                        .executes(context -> {
                            var location = context.getSource().getLocation();
                            var area = plugin.areaProvider().getArea(location);
                            return info(context, area, plugin);
                        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> list(ProtectPlugin plugin) {
        return Commands.literal("list")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.list"))
                .then(Commands.argument("provider", new FlagProviderArgumentType(plugin))
                        .executes(context -> {
                            var provider = context.getArgument("provider", Plugin.class);
                            return list(context, provider, plugin);
                        }))
                .executes(context -> {
                    plugin.flagRegistry().getRegistry().keySet()
                            .forEach(key -> list(context, key, plugin));
                    return Command.SINGLE_SUCCESS;
                });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> set(ProtectPlugin plugin) {
        var command = Commands.literal("set")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.set"));
        plugin.flagRegistry().getRegistry().values().forEach(flags -> flags.forEach(flag -> {
            command.then(Commands.literal(flag.key().asString())
                    .then(Commands.argument("value", flag.getArgumentType())
                            .then(Commands.argument("area", new AreaArgumentType(plugin))
                                    .executes(context -> {
                                        var area = context.getArgument("area", Area.class);
                                        return set(context, flag, area, plugin);
                                    }))
                            .executes(context -> {
                                var location = context.getSource().getLocation();
                                return set(context, flag, plugin.areaProvider().getArea(location), plugin);
                            })));
        }));
        return command;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(ProtectPlugin plugin) {
        return Commands.literal("reset")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.reset"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin, flag ->
                                plugin.areaProvider().getAreas().anyMatch(area -> area.hasFlag(flag))))
                        .then(Commands.argument("area", new AreaArgumentType(plugin, (context, area) -> {
                            var flag = (Flag<?>) context.getChild().getArgument("flag", Flag.class);
                            return area.hasFlag(flag);
                        })).executes(context -> {
                            var area = context.getArgument("area", Area.class);
                            return reset(context, area, plugin);
                        })).executes(context -> {
                            var location = context.getSource().getLocation();
                            return reset(context, plugin.areaProvider().getArea(location), plugin);
                        }));
    }

    private static int info(CommandContext<CommandSourceStack> context, Area area, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        plugin.bundle().sendMessage(sender, "area.flag.info",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(area.getFlag(flag))));
        return Command.SINGLE_SUCCESS;
    }

    private static int list(CommandContext<CommandSourceStack> context, Plugin provider, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var flags = plugin.flagRegistry().getFlags(provider);
        var components = flags.stream()
                .map(flag -> plugin.bundle().component("area.flag.list.format", sender,
                        Placeholder.parsed("flag", flag.key().asString())))
                .toList();
        plugin.bundle().sendMessage(sender, "area.flag.list",
                Placeholder.parsed("provider", provider.getName()),
                Formatter.number("amount", flags.size()),
                Formatter.joining("flags", components));
        return Command.SINGLE_SUCCESS;
    }

    private static <T> int set(CommandContext<CommandSourceStack> context, Flag<T> flag, Area area, ProtectPlugin plugin) {
        var value = context.getArgument("value", flag.getValueType());
        var message = area.setFlag(flag, value) ? "area.flag.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(value)));
        return Command.SINGLE_SUCCESS;
    }

    private static int reset(CommandContext<CommandSourceStack> context, Area area, ProtectPlugin plugin) {
        var flag = (Flag<?>) context.getArgument("flag", Flag.class);
        var message = area.removeFlag(flag) ? "area.flag.reset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()));
        return Command.SINGLE_SUCCESS;
    }
}
