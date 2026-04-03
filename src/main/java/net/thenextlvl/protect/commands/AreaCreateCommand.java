package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.exception.UnsupportedRegionException;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class AreaCreateCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("create")
                .requires(stack -> stack.getSender() instanceof final Player player
                                   && player.hasPermission("protect.command.area.create"))
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                .executes(context -> {
                                    final var priority = context.getArgument("priority", int.class);
                                    return execute(context, priority, plugin);
                                }))
                        .executes(context -> execute(context, 0, plugin)));
    }

    private static int execute(final CommandContext<CommandSourceStack> context, final int priority, final ProtectPlugin plugin) {
        final var player = (Player) context.getSource().getSender();
        final var name = context.getArgument("name", String.class);

        if (plugin.areaProvider().getArea(name).isPresent()) {
            plugin.bundle().sendMessage(player, "area.exists", Placeholder.parsed("area", name));
            return 0;
        }

        try {
            final var session = WorldEditPlugin.getInstance().getSession(player);
            final var creator = plugin.areaService().creator(
                    name, player.getWorld(), session.getSelection()
            ).priority(priority).create();
            plugin.bundle().sendMessage(player, "area.created",
                    Placeholder.parsed("area", creator.getName()));
            return Command.SINGLE_SUCCESS;
        } catch (final UnsupportedRegionException e) {
            plugin.bundle().sendMessage(player, "region.unsupported",
                    Placeholder.parsed("type", e.getType().getSimpleName()));
            return 0;
        } catch (final IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        } catch (final Exception e) {
            plugin.bundle().sendMessage(player, "command.error");
            plugin.getComponentLogger().error("Failed to create area {}", name, e);
            ProtectPlugin.ERROR_TRACKER.trackError(e);
            return 0;
        }
    }
}
