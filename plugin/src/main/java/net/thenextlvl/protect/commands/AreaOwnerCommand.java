package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.commands.argument.AreaArgumentType;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class AreaOwnerCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("owner")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.owner"))
                .then(Commands.literal("set")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.owner.set"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .executes(context -> set(context, plugin)))))
                .then(Commands.literal("remove")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.owner.remove"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin,
                                        (commandContext, area) -> area.getOwner().isPresent()))
                                .executes(context -> remove(context, plugin))));
    }

    private static int set(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) throws CommandSyntaxException {
        final var sender = context.getSource().getSender();
        final var area = context.getArgument("area", Area.class);
        final var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        final var player = resolver.resolve(context.getSource()).getFirst();
        final var success = area.setOwner(player.getUniqueId());
        final var message = success ? "area.owner.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("player", player.name().hoverEvent(player.asHoverEvent())),
                Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static int remove(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var area = context.getArgument("area", Area.class);
        final var success = area.setOwner(null);
        final var message = success ? "area.owner.remove" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
