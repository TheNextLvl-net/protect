package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.AreaArgumentType;

class AreaInfoCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("info")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.info"))
                .then(Commands.argument("area", new AreaArgumentType(plugin))
                        .executes(context -> {
                            var area = context.getArgument("area", Area.class);
                            return info(context, area, plugin);
                        }))
                .executes(context -> {
                    var location = context.getSource().getLocation();
                    var area = plugin.areaProvider().getArea(location);
                    return info(context, area, plugin);
                });
    }

    private static int info(CommandContext<CommandSourceStack> context, Area area, ProtectPlugin plugin) {
        var sender = context.getSource().getSender();
        var type = plugin.areaService().getAdapter(area.getClass()).key().asString();
        var flags = area.getFlags().entrySet().stream()
                .map(entry -> plugin.bundle().component("area.flag.format", sender,
                        Placeholder.parsed("flag", entry.getKey().key().asString()),
                        Placeholder.unparsed("value", String.valueOf(entry.getValue()))))
                .toList();

        plugin.bundle().sendMessage(sender, "area.info",
                Placeholder.component("flags", Component.join(JoinConfiguration.commas(true), flags)),
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("file", area.getFile().getPath()),
                Placeholder.parsed("priority", String.valueOf(area.getPriority())),
                Placeholder.parsed("size", String.valueOf(area.getFile().length() / 1024)),
                Placeholder.parsed("type", type),
                Placeholder.parsed("world", area.getWorld().key().asString()));

        if (!(area instanceof RegionizedArea<?> regionizedArea))
            return Command.SINGLE_SUCCESS;

        plugin.bundle().sendMessage(sender, "area.info.bounds",
                Placeholder.parsed("bounds", regionizedArea.getRegion().getMinimumPoint().toParserString()
                                             + " - " + regionizedArea.getRegion().getMaximumPoint().toParserString()));

        return Command.SINGLE_SUCCESS;
    }
}
