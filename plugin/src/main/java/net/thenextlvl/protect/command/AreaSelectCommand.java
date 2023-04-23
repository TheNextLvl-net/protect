package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.area.Area;
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
        var area = context.contains("area") ? Area.get(context.<String>get("area")) : Area.highestArea(player);
        if (area != null && !area.isGlobalArea())
            handleSelect(player, area);
        else if (area != null)
            player.sendRichMessage(Messages.INVALID_AREA.message(player.locale(), player));
        else player.sendRichMessage(JavaPlugin.getPlugin(Protect.class).formatter().format(
                    "%prefix% <red>/area select <dark_gray>(<gold>area<dark_gray>)"
            ));
    }

    private static void handleSelect(Player player, Area area) {
        var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
        var session = worldEdit.getSession(player);
        session.setRegionSelector(area.getRegion().getWorld(), new CuboidRegionSelector(
                area.getRegion().getWorld(),
                area.getRegion().getMinimumPoint(),
                area.getRegion().getMaximumPoint()
        ));
        session.updateServerCUI(worldEdit.wrapPlayer(player));
        player.sendRichMessage(Messages.AREA_SELECTED.message(player.locale(), player,
                Placeholder.of("area", area.getName())));
    }
}
