package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
class AreaTeleportCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("teleport", "tp")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof RegionizedArea<?>)
                                .map(Area::getName)
                                .filter(s -> s.startsWith(token))
                                .toList())
                        .build())
                .senderType(Player.class)
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var name = context.<String>get("area");
        // todo pattern validation
        var area = plugin.areaProvider().getArea(name).orElse(null);
        if (area instanceof RegionizedArea<?> regionizedArea) handleTeleport(player, regionizedArea);
        else if (area != null) plugin.bundle().sendMessage(player, "area.invalid");
        else plugin.bundle().sendMessage(player, "command.area.teleport");
    }

    private void handleTeleport(Player player, RegionizedArea<?> area) {
        var point = area.getRegion().getMaximumPoint();
        var location = new Location(area.getWorld(), point.getX() + 0.5, point.getY(), point.getZ() + 0.5);
        var block = location.getWorld().getHighestBlockAt(location);
        if (block.getY() < location.getY()) location.setY(block.getY() + 1);
        location.setPitch(player.getLocation().getPitch());
        location.setYaw(player.getLocation().getYaw());
        player.teleportAsync(location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(success -> {
            var message = success ? "area.teleport.success" : "area.teleport.fail";
            plugin.bundle().sendMessage(player, message, Placeholder.parsed("area", area.getName()));
        });
    }
}
