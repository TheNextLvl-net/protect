package net.thenextlvl.protect.command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.NamePattern;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaCreateCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("create")
                .permission("protect.command.area.create")
                .commandDescription(Description.description("create new areas"))
                .required("name", StringParser.greedyStringParser())
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSourceStack> context) {
        if (!(context.sender().getSender() instanceof Player player))
            throw new InvalidCommandSenderException(context.sender(), Player.class, List.of(), context.command());
        var name = context.<String>get("name");
        if (!name.matches(NamePattern.Regionized.PATTERN)) {
            plugin.bundle().sendMessage(player, "area.name.pattern",
                    Placeholder.parsed("pattern", NamePattern.Regionized.PATTERN));
            return;
        }
        var placeholder = Placeholder.parsed("area", name);
        if (plugin.areaProvider().getArea(name).isPresent())
            plugin.bundle().sendMessage(player, "area.exists", placeholder);
        else try {
            var session = JavaPlugin.getPlugin(WorldEditPlugin.class).getSession(player);
            var selection = session.getSelection();
            if (selection instanceof CuboidRegion cuboidRegion) {
                plugin.areaService().create(name, player.getWorld(), cuboidRegion);
            } else {
                plugin.bundle().sendMessage(player, "region.unsupported",
                        Placeholder.parsed("type", selection.getClass().getSimpleName()));
                return;
            }
            plugin.bundle().sendMessage(player, "area.created", placeholder);
        } catch (IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select", placeholder);
        }
    }
}
