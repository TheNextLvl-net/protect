package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
class AreaCreateCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("create")
                .argument(StringArgument.of("name"))
                .senderType(Player.class)
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var name = context.<String>get("name");
        // todo pattern validation
        var player = (Player) context.getSender();
        var placeholder = Placeholder.parsed("area", name);
        if (plugin.areaProvider().getArea(name).isPresent())
            plugin.bundle().sendMessage(player, "area.exists", placeholder);
        else try {
            var session = JavaPlugin.getPlugin(WorldEditPlugin.class).getSession(player);
            if (session.getSelection() instanceof CuboidRegion cuboidRegion) {
                plugin.areaService().create(name, cuboidRegion);
            } else throw new IncompleteRegionException();
            plugin.bundle().sendMessage(player, "area.created", placeholder);
        } catch (IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select", placeholder);
        }
    }
}
