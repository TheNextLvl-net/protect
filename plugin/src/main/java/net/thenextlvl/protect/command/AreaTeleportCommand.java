package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

class AreaTeleportCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("teleport", "tp")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().filter(area ->
                                !area.isGlobalArea()).map(Area::getName).filter(s ->
                                s.startsWith(token)).toList())
                        .build())
                .senderType(Player.class)
                .handler(AreaTeleportCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var area = Area.get(context.<String>get("area"));
        if (area != null && !area.isGlobalArea())
            handleTeleport(player, area);
        else if (area != null)
            player.sendRichMessage(Messages.INVALID_AREA.message(player.locale(), player));
        else player.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                    "%prefix% <red>/area teleport <dark_gray>[<gold>area<dark_gray>]"
            ));
    }

    private static void handleTeleport(Player player, Area area) {
        var point = area.getRegion().getMaximumPoint();
        var location = new Location(area.getWorld(), point.getX() + 0.5, point.getY(), point.getZ() + 0.5);
        var block = location.getWorld().getHighestBlockAt(location);
        if (block.getY() < location.getY()) location.setY(block.getY() + 1);
        location.setPitch(player.getLocation().getPitch());
        location.setYaw(player.getLocation().getYaw());
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(success -> {
            if (success) player.sendRichMessage(Messages.AREA_TELEPORT_SUCCEEDED.message(
                    player.locale(), player, Placeholder.of("area", area.getName())));
            else player.sendRichMessage(Messages.AREA_TELEPORT_FAILED.message(player.locale(),
                    player, Placeholder.of("area", area.getName())));
        });
    }
}
