package net.thenextlvl.protect.command;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.DefaultValue;
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
class AreaSelectCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("select")
                .permission("worldedit.selection.pos")
                .commandDescription(Description.description("(worldedit) select the region of an existing area"))
                .optional("area", StringParser.greedyStringParser(),
                        DefaultValue.dynamic(context -> plugin.areaProvider()
                                .getArea(context.sender().getLocation())
                                .getName()),
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
                new InvalidSyntaxException("area select [area]", context, List.of()));
        if (!(area instanceof RegionizedArea<?> regionizedArea)) {
            plugin.bundle().sendMessage(player, "area.invalid.select");
            return;
        }
        var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
        var session = worldEdit.getSession(player);
        var world = new BukkitWorld(area.getWorld());
        session.setRegionSelector(world, new CuboidRegionSelector(
                world,
                regionizedArea.getRegion().getMinimumPoint(),
                regionizedArea.getRegion().getMaximumPoint()
        ));
        session.updateServerCUI(worldEdit.wrapPlayer(player));
        plugin.bundle().sendMessage(player, "area.selected", Placeholder.parsed("area", area.getName()));
    }
}
