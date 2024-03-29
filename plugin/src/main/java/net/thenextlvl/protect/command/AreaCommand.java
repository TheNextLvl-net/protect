package net.thenextlvl.protect.command;

import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.function.Function;

import static cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType.*;

@RequiredArgsConstructor
public class AreaCommand {
    private final ProtectPlugin plugin;

    public void register() throws Exception {
        var manager = new PaperCommandManager<>(plugin, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        manager.commandSyntaxFormatter(new CustomSyntaxFormatter<>());
        manager.registerAsynchronousCompletions();
        registerExceptionHandlers(manager);
        manager.registerBrigadier();
        var builder = manager.commandBuilder("area").permission("protect.command.area");
        manager.command(new AreaCreateCommand(plugin).create(builder));
        manager.command(new AreaDeleteCommand(plugin).create(builder));
        manager.command(new AreaFlagCommand(plugin).create(builder));
        manager.command(new AreaInfoCommand(plugin).create(builder));
        manager.command(new AreaListCommand(plugin).create(builder));
        manager.command(new AreaPriorityCommand(plugin).create(builder));
        manager.command(new AreaRedefineCommand(plugin).create(builder));
        manager.command(new AreaSchematicCommand(plugin).create(builder));
        manager.command(new AreaSelectCommand(plugin).create(builder));
        manager.command(new AreaTeleportCommand(plugin).create(builder));
    }

    private void registerExceptionHandlers(PaperCommandManager<CommandSender> manager) {
        new MinecraftExceptionHandler<CommandSender>()
                .withHandler(INVALID_SYNTAX, e -> plugin.bundle().component(Locale.US, "command.syntax",
                        Placeholder.parsed("syntax", ((InvalidSyntaxException) e).getCorrectSyntax())))
                .withHandler(INVALID_SENDER, (sender, e) -> plugin.bundle().component(sender, "command.sender"))
                .withHandler(NO_PERMISSION, (sender, e) -> plugin.bundle().component(sender, "command.permission",
                        Placeholder.parsed("permission", ((NoPermissionException) e).getMissingPermission())))
                .withHandler(COMMAND_EXECUTION, (sender, e) -> plugin.bundle().component(sender, "command.exception",
                        Placeholder.parsed("command", "area")))
                .withArgumentParsingHandler()
                .apply(manager, sender -> sender);
    }
}
