package net.thenextlvl.protect.command;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

@RequiredArgsConstructor
class AreaPriorityCommand {
    private final ProtectPlugin plugin;

    Command.Builder<CommandSender> create(Command.Builder<CommandSender> builder) {
        return builder
                .literal("priority")
                .argument(IntegerArgument.of("priority"))
                .argument(StringArgument.<CommandSender>builder("area")
                        .withSuggestionsProvider((context, token) -> plugin.areaProvider().getAreas()
                                .map(Area::getName)
                                .filter(s -> s.startsWith(token))
                                .toList())
                        .asOptional()
                        .build())
                .handler(this::execute);
    }

    @SuppressWarnings("PatternValidation")
    private void execute(CommandContext<CommandSender> context) {
        var sender = context.getSender();
        var name = context.<String>get("area");
        // todo pattern validation
        var area = context.contains("area") ? plugin.areaProvider().getArea(name).orElse(null) :
                sender instanceof Entity entity ? plugin.areaProvider().getArea(entity) : null;
        if (area != null) setPriority(context, sender, area);
        else plugin.bundle().sendMessage(sender, "command.area.priority");
    }

    private void setPriority(CommandContext<CommandSender> context, CommandSender sender, Area area) {
        area.setPriority(context.<Integer>get("priority"));
        plugin.bundle().sendMessage(sender, "area.priority.changed",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("priority", String.valueOf(area.getPriority())));
    }
}
