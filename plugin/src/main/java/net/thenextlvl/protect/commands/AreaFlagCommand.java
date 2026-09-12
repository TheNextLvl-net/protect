package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.commands.argument.AreaArgumentType;
import net.thenextlvl.protect.commands.argument.FlagArgumentType;
import net.thenextlvl.protect.commands.argument.FlagProviderArgumentType;
import net.thenextlvl.protect.flag.CommandFlag;
import net.thenextlvl.protect.flag.Flag;
import net.thenextlvl.protect.flag.ValueFlag;
import net.thenextlvl.protect.flag.CollectionFlag;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
final class AreaFlagCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("flag")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag"))
                .then(info(plugin))
                .then(list(plugin))
                .then(set(plugin))
                .then(add(plugin))
                .then(remove(plugin))
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
            if (!(flag instanceof final ValueFlag<?> valueFlag) || !(flag instanceof final CommandFlag<?> commandFlag) || !commandFlag.supportsSet())
                return;
            command.then(Commands.literal(flag.key().asString())
                    .then(Commands.argument("value", commandFlag.argumentType())
                            .suggests(commandFlag::listSuggestions)
                            .then(Commands.argument("area", new AreaArgumentType(plugin))
                                    .executes(context -> {
                                        final var area = context.getArgument("area", Area.class);
                                        return set(context, commandFlag, valueFlag, area, plugin);
                                    }))
                            .executes(context -> {
                                final var location = context.getSource().getLocation();
                                return set(context, commandFlag, valueFlag, plugin.areaProvider().getArea(location), plugin);
                            })));
        }));
        return command;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> add(final ProtectPlugin plugin) {
        final var command = Commands.literal("add")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.set"));
        plugin.flagRegistry().getFlags().stream()
                .filter(flag -> flag instanceof final CollectionFlag<?> collectionFlag && collectionFlag.supportsAdd())
                .map(flag -> (CollectionFlag<?>) flag)
                .forEach(flag -> command.then(Commands.literal(flag.key().asString())
                        .then(Commands.argument("value", flag.argumentType())
                                .then(Commands.argument("area", new AreaArgumentType(plugin))
                                        .executes(context -> add(context, flag, context.getArgument("area", Area.class), plugin)))
                                .executes(context -> add(context, flag, plugin.areaProvider().getArea(context.getSource().getLocation()), plugin)))));
        return command;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> remove(final ProtectPlugin plugin) {
        final var command = Commands.literal("remove")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.set"));
        plugin.flagRegistry().getFlags().stream()
                .filter(flag -> flag instanceof final CollectionFlag<?> collectionFlag && collectionFlag.supportsRemove())
                .map(flag -> (CollectionFlag<?>) flag)
                .forEach(flag -> command.then(Commands.literal(flag.key().asString())
                        .then(Commands.argument("value", flag.argumentType())
                                .then(Commands.argument("area", new AreaArgumentType(plugin))
                                        .executes(context -> remove(context, flag, context.getArgument("area", Area.class), plugin)))
                                .executes(context -> remove(context, flag, plugin.areaProvider().getArea(context.getSource().getLocation()), plugin)))));
        return command;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> reset(final ProtectPlugin plugin) {
        return Commands.literal("reset")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.flag.reset"))
                .then(Commands.argument("flag", new FlagArgumentType(plugin, flag ->
                                plugin.areaProvider().getAreas().anyMatch(area -> area.hasFlag(flag))))
                        .then(Commands.argument("area", new AreaArgumentType(plugin, (context, area) -> {
                            final var flag = context.getLastChild().getArgument("flag", Flag.class);
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
        final var flag = context.getArgument("flag", Flag.class);
        final var value = flag instanceof final ValueFlag<?> valueFlag
                ? format(area, valueFlag)
                : "null";
        plugin.bundle().sendMessage(sender, "area.flag.info",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", value));
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
    private static int set(final CommandContext<CommandSourceStack> context, final CommandFlag commandFlag, final ValueFlag flag, final Area area, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var value = commandFlag.resolve(context);
        final var message = area.setFlag(flag.withValue(value)) ? "area.flag.set" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", String.valueOf(value)));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int add(final CommandContext<CommandSourceStack> context, final CollectionFlag flag, final Area area, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var value = flag.resolveElement(context);
        final CollectionFlag current = area.getFlag(() -> flag);
        final var values = new ArrayList((List<?>) current.getValue());
        final var changed = values.add(value) && area.setFlag(flag.withValue(List.copyOf(values)));
        plugin.bundle().sendMessage(context.getSource().getSender(), changed ? "area.flag.set" : "nothing.changed",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", flag.format(List.copyOf(values))));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int remove(final CommandContext<CommandSourceStack> context, final CollectionFlag flag, final Area area, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var value = flag.resolveElement(context);
        final CollectionFlag current = area.getFlag(() -> flag);
        final var values = new ArrayList((List<?>) current.getValue());
        final var changed = values.remove(value) && area.setFlag(flag.withValue(List.copyOf(values)));
        plugin.bundle().sendMessage(context.getSource().getSender(), changed ? "area.flag.set" : "nothing.changed",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()),
                Placeholder.unparsed("value", flag.format(List.copyOf(values))));
        return Command.SINGLE_SUCCESS;
    }

    private static int reset(final CommandContext<CommandSourceStack> context, final Area area, final ProtectPlugin plugin) {
        final var flag = context.getArgument("flag", Flag.class);
        final var message = area.removeFlag(flag) ? "area.flag.reset" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("flag", flag.key().asString()));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String format(final Area area, final ValueFlag flag) {
        final ValueFlag current = area.getFlag(() -> flag);
        return (String) current.getValue()
                .map(value -> current.format(current.cast(value)))
                .orElse("null");
    }
}
