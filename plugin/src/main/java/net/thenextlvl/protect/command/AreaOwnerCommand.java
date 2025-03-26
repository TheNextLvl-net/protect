package net.thenextlvl.protect.command;

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
import net.thenextlvl.protect.command.argument.AreaArgumentType;

class AreaOwnerCommand {
    private final ProtectPlugin plugin;

    AreaOwnerCommand(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("owner")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.owner"))
                .then(Commands.literal("set")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.owner.set"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin))
                                .then(Commands.argument("player", ArgumentTypes.player())
                                        .executes(this::set))))
                .then(Commands.literal("remove")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.owner.remove"))
                        .then(Commands.argument("area", new AreaArgumentType(plugin,
                                        (commandContext, area) -> area.getOwner().isPresent()))
                                .executes(this::remove)));
    }

    private int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
        var player = resolver.resolve(context.getSource()).getFirst();
        var success = area.setOwner(player.getUniqueId());
        var message = success ? "area.owner.set" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.component("player", player.name().hoverEvent(player.asHoverEvent())),
                Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int remove(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", Area.class);
        var success = area.setOwner(null);
        var message = success ? "area.owner.remove" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
