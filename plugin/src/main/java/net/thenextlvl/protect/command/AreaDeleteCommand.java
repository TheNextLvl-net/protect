package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaDeleteCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("delete")
                .permission("protect.command.area.delete")
                .commandDescription(Description.description("delete existing areas"))
                .required("area",
                        StringParser.greedyStringParser(),
                        SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                .filter(area -> area instanceof RegionizedArea<?>)
                                .map(Area::getName)
                                .map(Suggestion::suggestion)
                                .toList()))
                .handler(this::execute);
    }

    private void execute(CommandContext<CommandSourceStack> context) {
        var area = plugin.areaProvider().getArea(context.<String>get("area")).orElseThrow(() ->
                new InvalidSyntaxException("area delete [area]", context.sender(), List.of()));
        var placeholder = Placeholder.parsed("area", area.getName());
        if (!(area instanceof RegionizedArea<?> regionizedArea))
            plugin.bundle().sendMessage(context.sender().getSender(), "area.delete.invalid", placeholder);
        else if (regionizedArea.getSchematic().isFile())
            plugin.bundle().sendMessage(context.sender().getSender(), "area.schematic.delete");
        else if (plugin.areaService().delete(regionizedArea))
            plugin.bundle().sendMessage(context.sender().getSender(), "area.delete.success", placeholder);
        else plugin.bundle().sendMessage(context.sender().getSender(), "area.delete.failed", placeholder);
    }
}
