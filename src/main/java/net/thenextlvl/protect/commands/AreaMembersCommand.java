package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.commands.argument.AreaArgumentType;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@NullMarked
final class AreaMembersCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("members")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.members"))
                .then(Commands.literal("add")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.members.add"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .then(Commands.argument("players", ArgumentTypes.players())
                                        .executes(context -> addPlayers(context, plugin)))
                                .then(Commands.literal("permission")
                                        .then(Commands.argument("permission", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    final var area = context.getLastChild().getArgument("area", Area.class);
                                                    area.getPermissions().forEach(builder::suggest);
                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> addPermission(context, plugin))))))
                .then(Commands.literal("list")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.members.list"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .executes(context -> {
                                    final var area = context.getArgument("area", Area.class);
                                    return list(context, area, plugin);
                                }))
                        .executes(context -> {
                            final var area = plugin.areaProvider().getArea(context.getSource().getLocation());
                            return list(context, area, plugin);
                        }))
                .then(Commands.literal("remove")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.members.remove"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin, (commandContext, area) ->
                                        !area.getMembers().isEmpty() || !area.getPermissions().isEmpty()))
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            final var area = context.getLastChild().getArgument("area", Area.class);
                                            return CompletableFuture.runAsync(() -> area.getMembers().stream()
                                                            .map(plugin.getServer()::getOfflinePlayer)
                                                            .map(OfflinePlayer::getName)
                                                            .filter(Objects::nonNull)
                                                            .forEach(builder::suggest))
                                                    .thenApply(unused -> builder.build());
                                        }).executes(context -> removePlayer(context, plugin)))
                                .then(Commands.literal("permission")
                                        .then(Commands.argument("permission", StringArgumentType.word())
                                                .suggests((context, builder) -> {
                                                    final var area = context.getLastChild().getArgument("area", Area.class);
                                                    area.getPermissions().forEach(builder::suggest);
                                                    return builder.buildFuture();
                                                })
                                                .executes(context -> removePermission(context, plugin))))));
    }

    private static int list(final CommandContext<CommandSourceStack> context, final Area area, final ProtectPlugin plugin) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            final var sender = context.getSource().getSender();
            final var members = area.getMembers().stream()
                    .map(plugin.getServer()::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .map(Component::text)
                    .toList();
            final var permissions = area.getPermissions().stream()
                    .map(Component::text)
                    .toList();

            if (members.isEmpty() && permissions.isEmpty()) {
                plugin.bundle().sendMessage(sender, "area.members.none",
                        Placeholder.parsed("area", area.getName()));
                return;
            }

            if (!members.isEmpty()) plugin.bundle().sendMessage(sender, "area.members",
                    Placeholder.parsed("area", area.getName()),
                    Formatter.number("amount", members.size()),
                    Formatter.joining("members", members));

            if (!permissions.isEmpty()) plugin.bundle().sendMessage(sender, "area.members.permissions",
                    Placeholder.parsed("area", area.getName()),
                    Formatter.number("amount", permissions.size()),
                    Formatter.joining("permissions", permissions));
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int addPlayers(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var sender = context.getSource().getSender();
        final var area = context.getArgument("area", Area.class);
        final var resolver = context.getArgument("players", PlayerSelectorArgumentResolver.class);
        final var resolve = resolver.resolve(context.getSource());
        final var added = resolve.stream()
                .filter(player -> area.addMember(player.getUniqueId()))
                .toList();
        if (added.isEmpty()) plugin.bundle().sendMessage(sender, "nothing.changed");
        else added.forEach(player -> plugin.bundle().sendMessage(sender, "area.members.add",
                Placeholder.component("player", player.name().hoverEvent(player.asHoverEvent())),
                Placeholder.parsed("area", area.getName())));
        return added.size();
    }

        private static int addPermission(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
                final var sender = context.getSource().getSender();
                final var area = context.getArgument("area", Area.class);
                final var permission = context.getArgument("permission", String.class);
                if (!area.addPermission(permission)) {
                        plugin.bundle().sendMessage(sender, "nothing.changed");
                        return 0;
                }

                plugin.bundle().sendMessage(sender, "area.members.add.permission",
                                Placeholder.parsed("area", area.getName()),
                                Placeholder.parsed("permission", permission));
                return 1;
        }

        private static int removePlayer(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            final var sender = context.getSource().getSender();
            final var area = context.getArgument("area", Area.class);
            final var name = context.getArgument("player", String.class);
            final var player = plugin.getServer().getOfflinePlayer(name);
            final var success = area.removeMember(player.getUniqueId());
            final var message = success ? "area.members.remove" : "nothing.changed";
            plugin.bundle().sendMessage(sender, message,
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("player", player.getName() != null ? player.getName() : name));
        });
        return Command.SINGLE_SUCCESS;
    }

    private static int removePermission(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var area = context.getArgument("area", Area.class);
        final var permission = context.getArgument("permission", String.class);
        final var success = area.removePermission(permission);
        final var message = success ? "area.members.remove.permission" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("permission", permission));
        return Command.SINGLE_SUCCESS;
    }
}
