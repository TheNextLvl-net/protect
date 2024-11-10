package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.GroupedArea;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.GroupedAreaArgumentType;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class AreaGroupCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("group")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group"))
                .then(delete())
                .then(groupCreate())
                .then(inspect())
                .then(redefine())
                .then(remove());
    }

    private LiteralArgumentBuilder<CommandSourceStack> delete() {
        return Commands.literal("delete")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.delete"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .executes(this::delete));
    }

    private LiteralArgumentBuilder<CommandSourceStack> groupCreate() {
        return Commands.literal("create")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.create"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                regionizedArea -> !(regionizedArea instanceof GroupedArea)))
                        .executes(this::create));
    }

    private LiteralArgumentBuilder<CommandSourceStack> inspect() {
        return Commands.literal("inspect")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.inspect"))
                .then(Commands.argument("area", new GroupedAreaArgumentType(plugin))
                        .executes(this::inspect));
    }

    private LiteralArgumentBuilder<CommandSourceStack> redefine() {
        return Commands.literal("redefine")
                .requires(stack -> stack.getSender() instanceof Player player
                                   && player.hasPermission("protect.command.area.group.redefine"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .then(Commands.argument("region", StringArgumentType.string())
                                .suggests(suggestGroups())
                                .executes(this::redefine)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("remove")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.remove"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .then(Commands.argument("region", StringArgumentType.string())
                                .suggests(suggestGroups())
                                .executes(this::remove)));
    }

    private static SuggestionProvider<CommandSourceStack> suggestGroups() {
        return (context, builder) -> {
            var group = context.getChild().getArgument("group", GroupedArea.class);
            group.getRegion().getRegions().keySet().forEach(builder::suggest);
            return builder.buildFuture();
        };
    }

    private int remove(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("group", GroupedArea.class);
        var region = context.getArgument("region", String.class);
        var success = area.getRegion().removeRegion(region);
        var message = success ? "area.group.remove" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("area", area.getName()),
                Placeholder.parsed("region", region));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private int redefine(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int inspect(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int add(CommandContext<CommandSourceStack> context, String name) {
        var player = (Player) context.getSource().getSender();
        try {
            var session = WorldEditPlugin.getInstance().getSession(player);
            var region = session.getSelection();

            var group = context.getArgument("group", GroupedArea.class);

            if (!group.getRegion().getRegions().values().iterator().next().getClass().isInstance(region)) {
                plugin.bundle().sendMessage(player, "region.unsupported",
                        Placeholder.parsed("type", region.getClass().getSimpleName()));
                return 0;
            }

            var success = !group.getRegion().hasRegion(region)
                          && group.getRegion().addRegion(name, region);

            var message = success ? "area.group.add" : "nothing.changed";
            plugin.bundle().sendMessage(player, message,
                    Placeholder.parsed("area", group.getName()),
                    Placeholder.parsed("region", name));
            return success ? Command.SINGLE_SUCCESS : 0;
        } catch (IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        }
    }

    private int delete(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var group = context.getArgument("group", GroupedArea.class);

        if (!plugin.areaService().delete(group)) {
            plugin.bundle().sendMessage(sender, "area.group.delete.failed",
                    Placeholder.parsed("group", group.getName()));
            return 0;
        }

        var region = group.getRegion().getRegions().values().iterator().next();
        var area = plugin.areaService().creator(group)
                .region(region)
                .create();

        plugin.bundle().sendMessage(sender, "area.group.delete",
                Placeholder.parsed("group", area.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private int create(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = context.getArgument("area", RegionizedArea.class);

        if (!plugin.areaService().delete((RegionizedArea<?>) area)) {
            plugin.bundle().sendMessage(sender, "area.group.create.failed",
                    Placeholder.parsed("area", area.getName()));
            return 0;
        }

        var grouped = plugin.areaService().creator((RegionizedArea<?>) area)
                .region(new GroupedRegion(Map.of("root", area.getRegion())))
                .create();

        plugin.bundle().sendMessage(sender, "area.group.create",
                Placeholder.parsed("area", grouped.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
