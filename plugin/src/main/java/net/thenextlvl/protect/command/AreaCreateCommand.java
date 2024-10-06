package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.NamePattern;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaCreateCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("create")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("protect.command.area.create"))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(this::execute));
    }

    @SuppressWarnings("PatternValidation")
    private int execute(CommandContext<CommandSourceStack> context) {
        var player = (Player) context.getSource().getSender();
        var name = context.getArgument("name", String.class);

        if (!name.matches(NamePattern.Regionized.PATTERN)) {
            plugin.bundle().sendMessage(player, "area.name.pattern",
                    Placeholder.parsed("pattern", NamePattern.Regionized.PATTERN));
            return 0;
        }

        if (plugin.areaProvider().getArea(name).isPresent()) {
            plugin.bundle().sendMessage(player, "area.exists", Placeholder.parsed("area", name));
            return 0;
        }

        try {
            var session = WorldEditPlugin.getInstance().getSession(player);
            var selection = session.getSelection();

            if (selection instanceof CuboidRegion cuboidRegion) {
                plugin.areaService().create(name, player.getWorld(), cuboidRegion);
            } else {
                plugin.bundle().sendMessage(player, "region.unsupported",
                        Placeholder.parsed("type", selection.getClass().getSimpleName()));
                return 0;
            }

            plugin.bundle().sendMessage(player, "area.created", Placeholder.parsed("area", name));
            return Command.SINGLE_SUCCESS;

        } catch (IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select", Placeholder.parsed("area", name));
            return 0;
        }
    }
}
