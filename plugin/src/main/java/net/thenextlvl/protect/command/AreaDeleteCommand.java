package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
class AreaDeleteCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder.literal("delete")
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof RegionizedArea<?>)
                                .map(Area::getName)
                                .filter(s -> s.startsWith(token))
                                .toList())
                        .build())
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var name = context.<String>get("area");
        // todo pattern validation
        var area = plugin.areaProvider().getArea(name).orElse(null);
        var sender = context.getSender();
        var placeholder = Placeholder.parsed("area", name);
        if (area instanceof RegionizedArea<?> regionizedArea && plugin.areaService().delete(regionizedArea)) {
            plugin.bundle().sendMessage(sender, "area.delete.success", placeholder);
        } else if (area != null) {
            plugin.bundle().sendMessage(sender, "area.delete.failed", placeholder);
        } else plugin.bundle().sendMessage(sender, "area.exists.not", placeholder);
    }
}
