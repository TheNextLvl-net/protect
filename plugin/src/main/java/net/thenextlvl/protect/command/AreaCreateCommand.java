package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import core.api.placeholder.Placeholder;
import net.kyori.adventure.audience.Audience;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class AreaCreateCommand {

    static Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("create")
                .argument(StringArgument.of("name"))
                .senderType(Player.class)
                .handler(AreaCreateCommand::execute);
    }

    private static void execute(CommandContext<CommandSender> context) {
        var name = context.<String>get("name");
        var player = (Player) context.getSender();
        var placeholder = Placeholder.<Audience>of("area", name);
        if (Area.get(name) == null) try {
            var session = JavaPlugin.getPlugin(WorldEditPlugin.class).getSession(player);
            BlockVector3 pos1 = session.getSelection().getMinimumPoint();
            BlockVector3 pos2 = session.getSelection().getMaximumPoint();
            Area.create(name, player.getWorld(), pos1, pos2);
            player.sendRichMessage(Messages.AREA_CREATED.message(player.locale(), player, placeholder));
        } catch (IncompleteRegionException ignored) {
            player.sendRichMessage(Messages.SELECT_REGION.message(player.locale(), player, placeholder));
        }
        else player.sendRichMessage(Messages.AREA_EXISTS.message(player.locale(), player, placeholder));
    }
}
