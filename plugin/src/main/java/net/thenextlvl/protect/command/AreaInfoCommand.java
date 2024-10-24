package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.AreaArgumentType;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaInfoCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("info")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.info"))
                .then(Commands.argument("area", new AreaArgumentType(plugin))
                        .executes(context -> {
                            var area = context.getArgument("area", Area.class);
                            return info(context, area);
                        }))
                .executes(context -> {
                    var location = context.getSource().getLocation();
                    var area = plugin.areaProvider().getArea(location);
                    return info(context, area);
                });
    }

    private int info(CommandContext<CommandSourceStack> context, Area area) {
        var sender = context.getSource().getSender();

        plugin.bundle().sendMessage(sender, "area.info.name",
                Placeholder.parsed("area", area.getName()));
        plugin.bundle().sendMessage(sender, "area.info.world",
                Placeholder.parsed("world", area.getWorld().getName()));
        plugin.bundle().sendMessage(sender, "area.info.priority",
                Placeholder.parsed("priority", String.valueOf(area.getPriority())));
        plugin.bundle().sendMessage(sender, "area.info.type",
                Placeholder.parsed("type", plugin.areaService().getAdapter(area.getClass())
                        .key().asString()));
        plugin.bundle().sendMessage(sender, "area.info.flags",
                Placeholder.parsed("flags", area.getFlags().entrySet().stream()
                        .map(entry -> entry.getKey().key().asString() + "=" + entry.getValue())
                        .collect(Collectors.joining(", "))));
        if (!(area instanceof RegionizedArea<?> regionizedArea))
            return Command.SINGLE_SUCCESS;

        plugin.bundle().sendMessage(sender, "area.info.bounds",
                Placeholder.parsed("bounds", regionizedArea.getRegion().getMinimumPoint().toParserString()
                                             + " - " + regionizedArea.getRegion().getMaximumPoint().toParserString()));

        return Command.SINGLE_SUCCESS;
    }
}
