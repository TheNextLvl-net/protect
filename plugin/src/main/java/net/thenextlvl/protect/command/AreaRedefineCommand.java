package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import core.api.placeholder.Placeholder;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.api.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class AreaRedefineCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("redefine")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> Area.areas().filter(area -> !area.isGlobalArea())
                                .map(Area::getName).filter(s -> s.startsWith(token)).toList())
                        .build())
                .senderType(Player.class)
                .handler(AreaRedefineCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var area = Area.get(context.<String>get("area"));
        var plugin = JavaPlugin.getPlugin(Protect.class);
        if (area != null && area.isGlobalArea()) {
            player.sendRichMessage(Messages.INVALID_AREA.message(player.locale(), player));
        } else if (area == null) {
            player.sendRichMessage(plugin.formatter().format("%prefix% §c/area redefine §8[§6area§8]"));
        } else if (area.getSchematic().getFile().isFile()) {
            player.sendRichMessage(Messages.SCHEMATIC_EXISTS.message(player.locale(), player));
        } else try {
            var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
            var redefine = area.redefine(worldEdit.getSession(player).getSelection().clone());
            var placeholder = Placeholder.<Player>of("area", area.getName());
            var message = redefine ? Messages.AREA_REDEFINE_SUCCEEDED : Messages.AREA_REDEFINE_FAILED;
            player.sendRichMessage(message.message(player.locale(), player, placeholder));
        } catch (IncompleteRegionException e) {
            player.sendRichMessage(Messages.SELECT_REGION.message(player.locale(), player));
        }
    }
}
