package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaTeleportCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("teleport", "tp")
                .permission("protect.command.area.teleport")
                .commandDescription(Description.description("teleport yourself to an area"))
                .required("area", StringParser.greedyStringParser(),
                        SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof RegionizedArea<?>)
                                .map(Area::getName)
                                .map(Suggestion::suggestion)
                                .toList()))
                .handler(this::execute);
    }

    private void execute(CommandContext<CommandSourceStack> context) {
        if (!(context.sender().getSender() instanceof Player player))
            throw new InvalidCommandSenderException(context.sender(), Player.class, List.of(), context.command());
        var area = plugin.areaProvider().getArea(context.<String>get("area")).orElseThrow(() ->
                new InvalidSyntaxException("area teleport [area]", player, List.of()));
        if (!(area instanceof RegionizedArea<?> regionizedArea)) {
            plugin.bundle().sendMessage(player, "area.invalid.teleport");
            return;
        }
        var point = regionizedArea.getRegion().getMaximumPoint();
        var location = new Location(area.getWorld(), point.x() + 0.5, point.y(), point.z() + 0.5);
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
