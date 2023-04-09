package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.api.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class AreaSelectCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("select")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().filter(area ->
                                !area.isGlobalArea()).map(Area::getName).filter(s ->
                                s.startsWith(token)).toList())
                        .asOptional()
                        .build())
                .senderType(Player.class)
                .handler(AreaSelectCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var plugin = JavaPlugin.getPlugin(Protect.class);
        var area = context.contains("area") ? Area.get(context.<String>get("area")) : Area.highestArea(player);
        WorldEditPlugin worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
        if (area != null && !area.isGlobalArea()) {
            var session = worldEdit.getSession(player);
            session.setRegionSelector(area.getRegion().getWorld(), new CuboidRegionSelector(
                    area.getRegion().getWorld(),
                    area.getRegion().getMinimumPoint(),
                    area.getRegion().getMaximumPoint()
            ));
            session.updateServerCUI(worldEdit.wrapPlayer(player));
            player.sendRichMessage(Messages.AREA_SELECTED.message(player.locale(), player,
                    Placeholder.of("area", area.getName())));
        } else if (area != null) player.sendRichMessage(Messages.INVALID_AREA.message(player.locale(), player));
        else player.sendRichMessage(plugin.formatter().format(
                    "%prefix% §c/area select §8(§6area§8)"
            ));
    }
}
