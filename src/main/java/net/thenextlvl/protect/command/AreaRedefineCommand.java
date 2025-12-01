package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Files;

@NullMarked
final class AreaRedefineCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("redefine")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("protect.command.area.redefine"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(context -> redefine(context, plugin)));
    }

    @SuppressWarnings("unchecked")
    private static int redefine(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var player = (Player) context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        if (Files.isRegularFile(area.getSchematicFile())) {
            plugin.bundle().sendMessage(player, "area.schematic.delete");
            return 0;
        }
        try {
            var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
            var region = worldEdit.getSession(player).getSelection();
            var redefine = region.getClass().isInstance(area.getRegion())
                    ? area.setRegion(region) : null;
            var message = redefine == null ? "area.redefine.unsupported"
                    : redefine ? "area.redefine.success" : "area.redefine.fail";
            plugin.bundle().sendMessage(player, message,
                    Placeholder.parsed("area", area.getName()),
                    Placeholder.parsed("type", region.getClass().getSimpleName()));
            return Boolean.TRUE.equals(redefine) ? Command.SINGLE_SUCCESS : 0;
        } catch (IncompleteRegionException e) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        }
    }
}
