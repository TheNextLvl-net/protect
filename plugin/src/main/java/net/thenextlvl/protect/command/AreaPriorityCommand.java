package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaPriorityCommand {
    private final ProtectPlugin plugin;
    private final Command.Builder<CommandSourceStack> builder;

    Command.Builder<CommandSourceStack> create() {
        return builder.literal("priority")
                .permission("protect.command.area.priority")
                .commandDescription(Description.description("change the priority of areas"))
                .required("priority", IntegerParser.integerParser())
                .optional("area", StringParser.greedyStringParser(),
                        DefaultValue.dynamic(context -> {
                            if (!(context.sender().getSender() instanceof Player player)) return "";
                            return plugin.areaProvider().getArea(player).getName();
                        }),
                        SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                .map(Area::getName)
                                .map(Suggestion::suggestion)
                                .toList()))
                .handler(this::execute);
    }

    private void execute(CommandContext<CommandSourceStack> context) {
        var sender = context.sender().getSender();
        var area = plugin.areaProvider().getArea(context.<String>get("area")).orElseThrow(() ->
                new InvalidSyntaxException("area priority [priority] [area]", context.sender(), List.of()));
        var priority = context.<Integer>get("priority");
        if (area.setPriority(priority)) plugin.bundle().sendMessage(sender, "area.priority.changed",
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("priority", String.valueOf(area.getPriority())));
        else plugin.bundle().sendMessage(sender, "nothing.changed");
    }
}
