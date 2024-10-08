package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
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
import net.thenextlvl.protect.area.CuboidArea;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaRedefineCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("redefine")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("protect.command.area.redefine"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(this::redefine));
    }

    private int redefine(CommandContext<CommandSourceStack> context) {
        var player = (Player) context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        if (area.getSchematic().exists()) {
            plugin.bundle().sendMessage(player, "area.schematic.delete");
            return 0;
        }
        try {
            var redefine = false;
            var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
            var region = worldEdit.getSession(player).getSelection();
            if (region instanceof CuboidRegion cuboidRegion && area instanceof CuboidArea cuboid) {
                redefine = cuboid.setRegion(cuboidRegion);
            } else {
                plugin.bundle().sendMessage(player, "region.unsupported",
                        Placeholder.parsed("type", region.getClass().getSimpleName()));
                return 0;
            }
            var message = redefine ? "area.redefine.success" : "area.redefine.fail";
            plugin.bundle().sendMessage(player, message, Placeholder.parsed("area", area.getName()));
            return redefine ? Command.SINGLE_SUCCESS : 0;
        } catch (IncompleteRegionException e) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        }
    }
}
