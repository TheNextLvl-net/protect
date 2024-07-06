package net.thenextlvl.protect.command;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CuboidArea;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
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
class AreaRedefineCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("redefine")
                .permission("protect.command.area.redefine")
                .commandDescription(Description.description("redefine the region of areas"))
                .required("area",
                        StringParser.greedyStringParser(),
                        SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof SchematicHolder)
                                .map(Area::getName)
                                .map(Suggestion::suggestion)
                                .toList()))
                .handler(this::execute);
    }

    private void execute(CommandContext<CommandSourceStack> context) {
        if (!(context.sender().getSender() instanceof Player player))
            throw new InvalidCommandSenderException(context.sender(), Player.class, List.of(), context.command());
        var area = plugin.areaProvider().getArea(context.<String>get("area")).orElseThrow(() ->
                new InvalidSyntaxException("area redefine [area]", player, List.of()));
        if (!(area instanceof RegionizedArea<?> regionizedArea))
            plugin.bundle().sendMessage(player, "area.invalid.redefine");
        else if (regionizedArea.getSchematic().isFile())
            plugin.bundle().sendMessage(player, "area.schematic.delete.redefine");
        else try {
                var redefine = false;
                var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
                var region = worldEdit.getSession(player).getSelection();
                if (region instanceof CuboidRegion cuboidRegion && regionizedArea instanceof CuboidArea cuboidArea) {
                    redefine = cuboidArea.setRegion(cuboidRegion);
                } else {
                    plugin.bundle().sendMessage(player, "region.unsupported",
                            Placeholder.parsed("type", region.getClass().getSimpleName()));
                    return;
                }
                plugin.bundle().sendMessage(player, redefine ? "area.redefine.success" : "area.redefine.fail",
                        Placeholder.parsed("area", area.getName()));
            } catch (IncompleteRegionException e) {
                plugin.bundle().sendMessage(player, "region.select");
            }
    }
}
