package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.paper.PaperCommandManager;

@SuppressWarnings("UnstableApiUsage")
public class AreaCommand {
    private final PaperCommandManager<CommandSourceStack> commandManager;
    private final ProtectPlugin plugin;

    public AreaCommand(ProtectPlugin plugin) {
        this.commandManager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(plugin);
        MinecraftExceptionHandler.create(CommandSourceStack::getSender)
                .handler(InvalidSyntaxException.class, (formatter, context) -> plugin.bundle().miniMessage()
                        .deserialize("<prefix> <red>/" + context.exception().correctSyntax()
                                .replace("(", "<dark_gray>(<gold>").replace(")", "<dark_gray>)")
                                .replace("[", "<dark_gray>[<gold>").replace("]", "<dark_gray>]")
                                .replace("|", "<dark_gray>|<red>")))
                .handler(InvalidCommandSenderException.class, (formatter, exception) -> plugin.bundle()
                        .component(exception.context().sender().getSender(), "command.sender"))
                .handler(NoPermissionException.class, (formatter, exception) -> plugin.bundle().component(
                        exception.context().sender().getSender(), "command.permission", Placeholder.parsed("permission",
                                exception.exception().missingPermission().permissionString())))
                .registerTo(commandManager);
        commandManager.commandSyntaxFormatter(new CustomSyntaxFormatter<>(commandManager));
        this.plugin = plugin;
    }

    public void register() {
        var area = commandManager.commandBuilder("area")
                .commandDescription(Description.description("create, delete or manage areas"));
        commandManager.command(new AreaCreateCommand(plugin, area).create());
        commandManager.command(new AreaDeleteCommand(plugin, area).create());
        commandManager.command(new AreaFlagCommand.Info(plugin, area).create());
        commandManager.command(new AreaFlagCommand.List(plugin, area).create());
        commandManager.command(new AreaFlagCommand.Set(plugin, area).create());
        commandManager.command(new AreaFlagCommand.Unset(plugin, area).create());
        commandManager.command(new AreaInfoCommand(plugin, area).create());
        commandManager.command(new AreaListCommand(plugin, area).create());
        commandManager.command(new AreaPriorityCommand(plugin, area).create());
        commandManager.command(new AreaRedefineCommand(plugin, area).create());
        commandManager.command(new AreaSchematicCommand.Delete(plugin, area).create());
        commandManager.command(new AreaSchematicCommand.Load(plugin, area).create());
        commandManager.command(new AreaSchematicCommand.Save(plugin, area).create());
        commandManager.command(new AreaSelectCommand(plugin, area).create());
        commandManager.command(new AreaTeleportCommand(plugin, area).create());
    }
}
