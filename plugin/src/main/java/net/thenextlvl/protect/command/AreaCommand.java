package net.thenextlvl.protect.command;

import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.function.Function;

import static cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType.INVALID_SENDER;
import static cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType.INVALID_SYNTAX;

public class AreaCommand {

    public static void register(Protect plugin) throws Exception {
        var manager = new PaperCommandManager<>(plugin, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        new MinecraftExceptionHandler<CommandSender>().withDefaultHandlers()
                .withHandler(INVALID_SYNTAX, e -> {
                    var syntax = ((InvalidSyntaxException) e).getCorrectSyntax()
                            .replace("[", "§8(§6").replace("]", "§8)")
                            .replace("<", "§8[§6").replace(">", "§8]")
                            .replace("|", " §8|§c ");
                    return Component.text(Messages.PREFIX.message() + " §c/" + syntax);
                })
                .withHandler(INVALID_SENDER, (sender, exception) -> {
                    Locale locale = sender instanceof Player player ? player.locale() : Locale.forLanguageTag("en-US");
                    return Component.text(Messages.INVALID_SENDER.message(locale));
                })
                .apply(manager, sender -> sender);
        var builder = manager.commandBuilder("area").permission("area.command");
        manager.registerBrigadier();
        manager.command(AreaCreateCommand.create(builder));
        manager.command(AreaDeleteCommand.create(builder));
        manager.command(AreaFlagCommand.create(builder));
        manager.command(AreaInfoCommand.create(builder));
        manager.command(AreaListCommand.create(builder));
        manager.command(AreaPriorityCommand.create(builder));
        manager.command(AreaRedefineCommand.create(builder));
        manager.command(AreaSchematicCommand.create(builder));
        manager.command(AreaSelectCommand.create(builder));
        manager.command(AreaTeleportCommand.create(builder));
    }
}
