package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.commands.argument.RegionizedAreaArgumentType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class AreaTeleportCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("teleport")
                .requires(stack -> stack.getSender() instanceof final Player player
                                   && player.hasPermission("protect.command.area.teleport"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(context -> teleport(context, plugin)));
    }

    private static int teleport(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var player = (Player) context.getSource().getSender();
        final var area = context.getArgument("area", RegionizedArea.class);
        player.teleportAsync(area.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(success -> {
            final var message = success ? "area.teleport.success" : "area.teleport.fail";
            plugin.bundle().sendMessage(player, message, Placeholder.parsed("area", area.getName()));
        });
        return Command.SINGLE_SUCCESS;
    }
}
