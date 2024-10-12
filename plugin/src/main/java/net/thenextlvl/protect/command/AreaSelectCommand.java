package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class AreaSelectCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("select")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("worldedit.selection.pos"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(this::select));
    }

    private int select(CommandContext<CommandSourceStack> context) {
        var player = (Player) context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);
        var session = WorldEditPlugin.getInstance().getSession(player);
        var world = new BukkitWorld(area.getWorld());
        session.setRegionSelector(world, switch (area.getRegion()) {
            case CuboidRegion cuboid -> new CuboidRegionSelector(world,
                    cuboid.getMinimumPoint(),
                    cuboid.getMaximumPoint()
            );
            case CylinderRegion cylinder -> new CylinderRegionSelector(
                    world, cylinder.getCenter().toVector2().toBlockPoint(),
                    cylinder.getRadius(), cylinder.getMinimumY(), cylinder.getMaximumY()
            );
            case EllipsoidRegion ellipsoid -> new EllipsoidRegionSelector(world,
                    ellipsoid.getCenter().toBlockPoint(),
                    ellipsoid.getRadius()
            );
            default -> throw new IllegalStateException("Unexpected value: " + area);
        });
        session.updateServerCUI(WorldEditPlugin.getInstance().wrapPlayer(player));
        plugin.bundle().sendMessage(player, "area.selected", Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
