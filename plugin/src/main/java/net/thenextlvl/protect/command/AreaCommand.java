package net.thenextlvl.protect.command;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;

public class AreaCommand extends PaperCommandManager<CommandSender> {
    private final ProtectPlugin plugin;

    public AreaCommand(ProtectPlugin plugin) throws InitializationException {
        super(plugin, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity());
        MinecraftExceptionHandler.<CommandSender>create(sender -> sender)
                .handler(InvalidSyntaxException.class, (formatter, context) -> plugin.bundle().miniMessage()
                        .deserialize("<prefix> <red>/" + context.exception().correctSyntax()
                                .replace("(", "<dark_gray>(<gold>").replace(")", "<dark_gray>)")
                                .replace("[", "<dark_gray>[<gold>").replace("]", "<dark_gray>]")
                                .replace("|", "<dark_gray>|<red>")))
                .handler(InvalidCommandSenderException.class, (formatter, exception) -> plugin.bundle()
                        .component(exception.context().sender(), "command.sender"))
                .handler(NoPermissionException.class, (formatter, exception) -> plugin.bundle().component(
                        exception.context().sender(), "command.permission", Placeholder.parsed("permission",
                                exception.exception().missingPermission().permissionString())))
                .registerTo(this);
        commandSyntaxFormatter(new CustomSyntaxFormatter<>(this));
        registerAsynchronousCompletions();
        registerBrigadier();
        this.plugin = plugin;
    }

    public void register() {
        var area = commandBuilder("area")
                .commandDescription(Description.description("create, delete or manage areas"));
        command(new AreaCreateCommand(plugin, area).create());
        command(new AreaDeleteCommand(plugin, area).create());
        command(new AreaFlagCommand.Info(plugin, area).create());
        command(new AreaFlagCommand.List(plugin, area).create());
        command(new AreaFlagCommand.Set(plugin, area).create());
        command(new AreaFlagCommand.Unset(plugin, area).create());
        command(new AreaInfoCommand(plugin, area).create());
        command(new AreaListCommand(plugin, area).create());
        command(new AreaPriorityCommand(plugin, area).create());
        command(new AreaRedefineCommand(plugin, area).create());
        command(new AreaSchematicCommand.Delete(plugin, area).create());
        command(new AreaSchematicCommand.Load(plugin, area).create());
        command(new AreaSchematicCommand.Save(plugin, area).create());
        command(new AreaSelectCommand(plugin, area).create());
        command(new AreaTeleportCommand(plugin, area).create());
    }
}
