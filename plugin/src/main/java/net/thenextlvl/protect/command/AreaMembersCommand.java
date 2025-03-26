package net.thenextlvl.protect.command;

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
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;
import org.bukkit.OfflinePlayer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

class AreaMembersCommand {
    private final ProtectPlugin plugin;

    AreaMembersCommand(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("members")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.members"))
                .then(Commands.literal("add")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.members.add"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .then(Commands.argument("players", ArgumentTypes.players())
                                        .executes(this::add))))
                .then(Commands.literal("list")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.members.list"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .executes(context -> {
                                    var area = context.getArgument("area", Area.class);
                                    return list(context, area);
                                }))
                        .executes(context -> {
                            var area = plugin.areaProvider().getArea(context.getSource().getLocation());
                            return list(context, area);
                        }))
                .then(Commands.literal("remove")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.members.remove"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin, (commandContext, area) ->
                                        !area.getMembers().isEmpty()))
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            var area = context.getChild().getArgument("area", Area.class);
                                            return CompletableFuture.runAsync(() -> area.getMembers().stream()
                                                            .map(plugin.getServer()::getOfflinePlayer)
                                                            .map(OfflinePlayer::getName)
                                                            .filter(Objects::nonNull)
                                                            .forEach(builder::suggest))
                                                    .thenApply(unused -> builder.build());
                                        }).executes(this::remove))));
    }

    private int list(CommandContext<CommandSourceStack> context, Area area) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            var sender = context.getSource().getSender();
            var members = area.getMembers().stream()
                    .map(plugin.getServer()::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .map(Component::text)
                    .toList();
            var message = members.isEmpty() ? "area.members.none" : "area.members";
            plugin.bundle().sendMessage(sender, message,
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("amount", String.valueOf(members.size())),
                    Placeholder.component("members", Component.join(JoinConfiguration.commas(true), members)));
        });
        return Command.SINGLE_SUCCESS;
    }

    private int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        var resolver = context.getArgument("players", PlayerSelectorArgumentResolver.class);
        var resolve = resolver.resolve(context.getSource());
        var added = resolve.stream()
                .filter(player -> area.addMember(player.getUniqueId()))
                .toList();
        if (added.isEmpty()) plugin.bundle().sendMessage(sender, "nothing.changed");
        else added.forEach(player -> plugin.bundle().sendMessage(sender, "area.members.add",
                Placeholder.component("player", player.name().hoverEvent(player.asHoverEvent())),
                Placeholder.parsed("area", area.getName())));
        return added.size();
    }

    private int remove(CommandContext<CommandSourceStack> context) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            var sender = context.getSource().getSender();
            var area = context.getArgument("area", Area.class);
            var name = context.getArgument("player", String.class);
            var player = plugin.getServer().getOfflinePlayer(name);
            var success = area.removeMember(player.getUniqueId());
            var message = success ? "area.members.remove" : "nothing.changed";
            plugin.bundle().sendMessage(sender, message,
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("player", player.getName() != null ? player.getName() : name));
        });
        return Command.SINGLE_SUCCESS;
    }
}
