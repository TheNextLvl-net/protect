package net.thenextlvl.protect.command;

import com.sk89q.worldedit.WorldEditException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.schematic.SchematicHolder;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
abstract class AreaSchematicCommand {
    protected final ProtectPlugin plugin;
    protected final Command.Builder<CommandSourceStack> builder;

    abstract Command.Builder<CommandSourceStack> create();

    protected abstract void execute(CommandSender sender, RegionizedArea<?> area);

    protected abstract String usage();

    protected final Command.Builder<CommandSourceStack> schematicCommand() {
        return builder.literal("schematic")
                .commandDescription(Description.description("save, load or delete schematics"));
    }

    protected final void execute(CommandContext<CommandSourceStack> context) {
        var area = plugin.areaProvider().getArea(context.<String>get("area")).orElse(null);
        if (!(area instanceof RegionizedArea<?> regionizedArea))
            throw new InvalidSyntaxException(usage(), context.sender(), List.of());
        execute(context.sender().getSender(), regionizedArea);
    }

    static class Delete extends AreaSchematicCommand {
        public Delete(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return schematicCommand().literal("delete")
                    .permission("protect.command.area.schematic.delete")
                    .commandDescription(Description.description("delete an area's schematic"))
                    .required("area", StringParser.greedyStringParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .filter(area -> area instanceof SchematicHolder holder
                                                    && holder.getSchematic().isFile())
                                    .map(Area::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandSender sender, RegionizedArea<?> area) {
            var message = area.deleteSchematic() ? "area.schematic.delete.success" : "area.schematic.delete.failed";
            plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getName()));
        }

        @Override
        protected String usage() {
            return "area schematic delete [area]";
        }
    }

    static class Load extends AreaSchematicCommand {
        public Load(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return schematicCommand().literal("load")
                    .permission("protect.command.area.schematic.load")
                    .commandDescription(Description.description("load in an area's schematic"))
                    .required("area", StringParser.greedyStringParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .filter(area -> area instanceof SchematicHolder holder
                                                    && holder.getSchematic().isFile())
                                    .map(Area::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandSender sender, RegionizedArea<?> area) {
            if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
            var load = false;
            try {
                load = area.loadSchematic();
            } catch (IOException | WorldEditException e) {
                plugin.getComponentLogger().error("Failed to load area schematic", e);
            }
            var message = load ? "area.schematic.load.success" : "area.schematic.load.failed";
            plugin.bundle().sendMessage(sender, message, Placeholder.parsed("schematic", area.getName()));
        }

        @Override
        protected String usage() {
            return "area schematic load [area]";
        }
    }

    static class Save extends AreaSchematicCommand {
        public Save(ProtectPlugin plugin, Command.Builder<CommandSourceStack> builder) {
            super(plugin, builder);
        }

        @Override
        Command.Builder<CommandSourceStack> create() {
            return schematicCommand().literal("save")
                    .permission("protect.command.area.schematic.save")
                    .commandDescription(Description.description("save an area's schematic"))
                    .required("area", StringParser.greedyStringParser(),
                            SuggestionProvider.blocking((context, input) -> plugin.areaProvider().getAreas()
                                    .filter(area -> area instanceof SchematicHolder)
                                    .map(Area::getName)
                                    .map(Suggestion::suggestion)
                                    .toList()))
                    .handler(this::execute);
        }

        @Override
        protected void execute(CommandSender sender, RegionizedArea<?> area) {
            if (area.isTooBig()) plugin.bundle().sendMessage(sender, "area.warning.size");
            try {
                area.saveSchematic();
                plugin.bundle().sendMessage(sender, "area.schematic.save.success",
                        Placeholder.parsed("schematic", area.getName()));
            } catch (IOException | WorldEditException e) {
                plugin.bundle().sendMessage(sender, "area.schematic.save.failed",
                        Placeholder.parsed("schematic", area.getName()));
                plugin.getComponentLogger().error("Failed dto save area schematic", e);
            }
        }

        @Override
        protected String usage() {
            return "area schematic save [area]";
        }
    }
}
