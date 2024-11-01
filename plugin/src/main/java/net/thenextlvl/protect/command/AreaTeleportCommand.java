package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaTeleportCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("teleport")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("protect.command.area.teleport"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(this::teleport));
    }

    private int teleport(CommandContext<CommandSourceStack> context) {
        var player = (Player) context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        var point = area.getRegion().getMaximumPoint();
        var location = new Location(area.getWorld(), point.x() + 0.5, point.y(), point.z() + 0.5);
        var block = location.getWorld().getHighestBlockAt(location);
        if (block.getY() < location.getY()) location.setY(block.getY() + 1);
        location.setPitch(player.getLocation().getPitch());
        location.setYaw(player.getLocation().getYaw());
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(success -> {
            var message = success ? "area.teleport.success" : "area.teleport.fail";
            plugin.bundle().sendMessage(player, message, Placeholder.parsed("area", area.getName()));
        });
        return Command.SINGLE_SUCCESS;
    }
}
