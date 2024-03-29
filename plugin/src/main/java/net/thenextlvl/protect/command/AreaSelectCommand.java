package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
class AreaSelectCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("select")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof RegionizedArea<?>)
                                .map(Area::getName)
                                .filter(s -> s.startsWith(token)).toList())
                        .asOptional()
                        .build())
                .senderType(Player.class)
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var player = (Player) context.getSender();
        var name = context.<String>getOrDefault("area", null);
        if (name != null && !name.matches("^@?[a-zA-Z0-9_-]+$")) {
            // todo pattern validation
            return;
        }
        var area = name != null ? plugin.areaProvider().getArea(name).orElse(null) : plugin.areaProvider().getArea(player);
        if (area instanceof RegionizedArea<?> regionizedArea)
            handleSelect(player, regionizedArea);
        else if (area != null)
            plugin.bundle().sendMessage(player, "area.invalid");
        else plugin.bundle().sendRawMessage(player, "command.area.select");
    }

    private void handleSelect(Player player, RegionizedArea<?> area) {
        var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
        var session = worldEdit.getSession(player);
        session.setRegionSelector(area.getRegion().getWorld(), new CuboidRegionSelector(
                area.getRegion().getWorld(),
                area.getRegion().getMinimumPoint(),
                area.getRegion().getMaximumPoint()
        ));
        session.updateServerCUI(worldEdit.wrapPlayer(player));
        plugin.bundle().sendMessage(player, "area.selected", Placeholder.parsed("area", area.getName()));
    }
}
