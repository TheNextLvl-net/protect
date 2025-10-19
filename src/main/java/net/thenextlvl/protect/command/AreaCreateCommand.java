package net.thenextlvl.protect.command;

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

class AreaCreateCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("create")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("protect.command.area.create"))
                .then(Commands.argument("name", StringArgumentType.string())
                        .then(Commands.argument("priority", IntegerArgumentType.integer())
                                .executes(context -> {
                                    var priority = context.getArgument("priority", int.class);
                                    return execute(context, priority, plugin);
                                }))
                        .executes(context -> execute(context, 0, plugin)));
    }

    private static int execute(CommandContext<CommandSourceStack> context, int priority, ProtectPlugin plugin) {
        var player = (Player) context.getSource().getSender();
        var name = context.getArgument("name", String.class);

        if (plugin.areaProvider().getArea(name).isPresent()) {
            plugin.bundle().sendMessage(player, "area.exists", Placeholder.parsed("area", name));
            return 0;
        }

        try {
            var session = WorldEditPlugin.getInstance().getSession(player);
            var creator = plugin.areaService().creator(
                    name, player.getWorld(), session.getSelection()
            ).priority(priority).create();
            plugin.bundle().sendMessage(player, "area.created",
                    Placeholder.parsed("area", creator.getName()));
            return Command.SINGLE_SUCCESS;
        } catch (UnsupportedRegionException e) {
            plugin.bundle().sendMessage(player, "region.unsupported",
                    Placeholder.parsed("type", e.getType().getSimpleName()));
            return 0;
        } catch (IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        } catch (Exception e) {
            plugin.bundle().sendMessage(player, "command.error");
            return 0;
        }
    }
}
