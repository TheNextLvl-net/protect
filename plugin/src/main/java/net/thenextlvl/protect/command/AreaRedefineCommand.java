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
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.CuboidArea;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
class AreaRedefineCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("redefine")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof SchematicHolder)
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
        // todo match name pattern
        var area = plugin.areaProvider().getArea(name).orElse(null);
        if (area == null) plugin.bundle().sendMessage(player, "command.area.redefine");
        else if (!(area instanceof RegionizedArea<?> regionizedArea))
            plugin.bundle().sendMessage(player, "area.invalid");
        else if (regionizedArea.getSchematic().isFile())
            plugin.bundle().sendMessage(player, "area.schematic.exists");
        else try {
                var redefine = false;
                var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
                var region = worldEdit.getSession(player).getSelection();
                if (region instanceof CuboidRegion cuboidRegion && regionizedArea instanceof CuboidArea cuboidArea) {
                    redefine = cuboidArea.redefine(cuboidRegion);
                }
                plugin.bundle().sendMessage(player, redefine ? "area.redefine.success" : "area.redefine.fail",
                        Placeholder.parsed("area", area.getName()));
            } catch (IncompleteRegionException e) {
                plugin.bundle().sendMessage(player, "region.select");
            }
    }
}
