package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaInfoCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("info")
                .permission("protect.command.area.info")
                .commandDescription(Description.description("query information about specific areas"))
                .optional("area",
                        StringParser.greedyStringParser(),
                        DefaultValue.dynamic(context -> {
                            if (!(context.sender() instanceof Player player)) return "";
                            return plugin.areaProvider().getArea(player).getName();
                        }),
                        SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                .map(Area::getName)
                                .map(Suggestion::suggestion)
                                .toList()))
                .handler(this::execute);
    }

    private void execute(CommandContext<CommandSourceStack> context) {
        var area = plugin.areaProvider().getArea(context.<String>get("area")).orElseThrow(() ->
                new InvalidSyntaxException("area info [area]", context.sender(), List.of()));
        plugin.bundle().sendMessage(context.sender().getSender(), "area.info.name",
                Placeholder.parsed("area", area.getName()));
        plugin.bundle().sendMessage(context.sender().getSender(), "area.info.world",
                Placeholder.parsed("world", area.getWorld().getName()));
        plugin.bundle().sendMessage(context.sender().getSender(), "area.info.priority",
                Placeholder.parsed("priority", String.valueOf(area.getPriority())));
        plugin.bundle().sendMessage(context.sender().getSender(), "area.info.type",
                Placeholder.parsed("type", plugin.areaService().getAdapter(area.getClass())
                        .getNamespacedKey().asString()));
        plugin.bundle().sendMessage(context.sender().getSender(), "area.info.flags",
                Placeholder.parsed("flags", area.getFlags().entrySet().stream()
                        .map(entry -> entry.getKey().key().asString() + "=" + entry.getValue())
                        .collect(Collectors.joining(", "))));
        if (!(area instanceof RegionizedArea<?> regionizedArea)) return;
        plugin.bundle().sendMessage(context.sender().getSender(), "area.info.bounds",
                Placeholder.parsed("bounds", regionizedArea.getRegion().getMinimumPoint().toParserString()
                                             + " - " + regionizedArea.getRegion().getMaximumPoint().toParserString()));
    }
}
