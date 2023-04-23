package net.thenextlvl.protect.command;

import cloud.commandframework.CommandTree;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.exceptions.InvalidSyntaxException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import core.api.placeholder.Placeholder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.thenextlvl.protect.Protect;
import net.thenextlvl.protect.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Function;

import static cloud.commandframework.minecraft.extras.MinecraftExceptionHandler.ExceptionType.*;

public class AreaCommand {

    private static CommandTree.@Nullable Node<@Nullable CommandArgument<CommandSender, ?>> node;

    public static void register(Protect plugin) throws Exception {
        var manager = new PaperCommandManager<>(plugin, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
        manager.commandSyntaxFormatter(new CustomSyntaxFormatter<>());
        manager.registerAsynchronousCompletions();
        registerExceptionHandlers(manager);
        manager.registerBrigadier();
        var builder = manager.commandBuilder("area").permission("area.command");
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

    private static void registerExceptionHandlers(PaperCommandManager<CommandSender> manager) {
        new MinecraftExceptionHandler<CommandSender>()
                .withHandler(INVALID_SYNTAX, e -> {
                    var syntax = ((InvalidSyntaxException) e).getCorrectSyntax();
                    return MiniMessage.miniMessage().deserialize(Messages.PREFIX.message() + " <red>/" + syntax);
                })
                .withHandler(INVALID_SENDER, (sender, exception) -> {
                    var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
                    return MiniMessage.miniMessage().deserialize(Messages.INVALID_SENDER.message(locale, sender));
                })
                .withHandler(NO_PERMISSION, (sender, exception) -> {
                    var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
                    return MiniMessage.miniMessage().deserialize(Messages.NO_PERMISSION.message(locale, sender));
                })
                .withHandler(COMMAND_EXECUTION, (sender, exception) -> {
                    var locale = sender instanceof Player player ? player.locale() : Messages.ENGLISH;
                    var command = Placeholder.<Audience>of("command", "area");
                    return MiniMessage.miniMessage().deserialize(Messages.COMMAND_EXCEPTION.message(locale, sender, command));
                })
                .withArgumentParsingHandler()
                .apply(manager, sender -> sender);
    }
}
