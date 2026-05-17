package net.thenextlvl.protect.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GroupedArea;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.commands.argument.GroupedAreaArgumentType;
import net.thenextlvl.protect.commands.argument.RegionizedAreaArgumentType;
import net.thenextlvl.protect.region.GroupedRegion;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
final class AreaGroupCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(final ProtectPlugin plugin) {
        return Commands.literal("group")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group"))
                .then(add(plugin))
                .then(delete(plugin))
                .then(groupCreate(plugin))
                .then(list(plugin))
                .then(redefine(plugin))
                .then(remove(plugin))
                .then(select(plugin));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add(final ProtectPlugin plugin) {
        return Commands.literal("add")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.add"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    final var name = context.getArgument("name", String.class);
                                    return add(context, name, plugin);
                                }))
                        .executes(context -> {
                            final var group = context.getArgument("group", GroupedArea.class);
                            final var name = "region-" + (group.getRegion().getRegions().size() + 1);
                            return add(context, name, plugin);
                        }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> delete(final ProtectPlugin plugin) {
        return Commands.literal("delete")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.delete"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .executes(context -> delete(context, plugin)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> groupCreate(final ProtectPlugin plugin) {
        return Commands.literal("create")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.create"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                regionizedArea -> !(regionizedArea instanceof GroupedArea)))
                        .executes(context -> create(context, plugin)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> list(final ProtectPlugin plugin) {
        return Commands.literal("list")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.list"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .executes(context -> list(context, plugin)))
                .executes(context -> listAll(context, plugin));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> redefine(final ProtectPlugin plugin) {
        return Commands.literal("redefine")
                .requires(stack -> stack.getSender() instanceof final Player player
                        && player.hasPermission("protect.command.area.group.redefine"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .then(Commands.argument("region", StringArgumentType.string())
                                .suggests(suggestGroups(plugin))
                                .executes(context -> redefine(context, plugin))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> remove(final ProtectPlugin plugin) {
        return Commands.literal("remove")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.remove"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .then(Commands.argument("region", StringArgumentType.string())
                                .suggests(suggestGroups(plugin))
                                .executes(context -> remove(context, plugin))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> select(final ProtectPlugin plugin) {
        return Commands.literal("select")
                .requires(stack -> stack.getSender().hasPermission("worldedit.selection.pos"))
                .then(Commands.argument("group", new GroupedAreaArgumentType(plugin))
                        .then(Commands.argument("region", StringArgumentType.string())
                                .suggests(suggestGroups(plugin))
                                .executes(context -> select(context, plugin))));
    }

    private static SuggestionProvider<CommandSourceStack> suggestGroups(final ProtectPlugin plugin) {
        return (context, builder) -> {
            final var group = context.getLastChild().getArgument("group", GroupedArea.class);
            group.getRegion().getRegions().keySet().forEach(builder::suggest);
            return builder.buildFuture();
        };
    }

    private static int remove(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var group = context.getArgument("group", GroupedArea.class);
        final var region = context.getArgument("region", String.class);
        final var success = group.getRegion().removeRegion(region);
        final var message = success ? "area.group.remove" : "nothing.changed";
        plugin.bundle().sendMessage(sender, message,
                Placeholder.parsed("area", group.getName()),
                Placeholder.parsed("region", region));
        return success ? Command.SINGLE_SUCCESS : 0;
    }

    private static int list(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var group = context.getArgument("group", GroupedArea.class);
        final var regions = group.getRegion().getRegions().keySet().stream()
                .map(Component::text)
                .toList();
        plugin.bundle().sendMessage(sender, "area.list.regions",
                Formatter.number("amount", regions.size()),
                Placeholder.parsed("group", group.getName()),
                Formatter.joining("regions", regions));
        return Command.SINGLE_SUCCESS;
    }

    private static int listAll(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var groups = plugin.areaProvider().getAreas()
                .filter(GroupedArea.class::isInstance)
                .map(Area::getName)
                .map(Component::text)
                .toList();
        plugin.bundle().sendMessage(sender, "area.list.groups",
                Formatter.number("amount", groups.size()),
                Formatter.joining("groups", groups));
        return Command.SINGLE_SUCCESS;
    }

    private static int redefine(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var player = (Player) context.getSource().getSender();
        final var group = context.getArgument("group", GroupedArea.class);
        final var name = context.getArgument("region", String.class);
        try {
            final var worldEdit = JavaPlugin.getPlugin(WorldEditPlugin.class);
            final var region = worldEdit.getSession(player).getSelection();
            final var redefine = region.getClass().isInstance(group.getRegion().getRegion(name));
            final var message = redefine ? "area.redefine.success.group" : "area.redefine.unsupported";
            if (redefine) group.getRegion().setRegion(name, region);
            plugin.bundle().sendMessage(player, message,
                    Placeholder.parsed("group", group.getName()),
                    Placeholder.parsed("region", name),
                    Placeholder.parsed("type", region.getClass().getSimpleName()));
            return redefine ? Command.SINGLE_SUCCESS : 0;
        } catch (final IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        }
    }

    private static int add(final CommandContext<CommandSourceStack> context, final String name, final ProtectPlugin plugin) {
        final var player = (Player) context.getSource().getSender();
        try {
            final var session = WorldEditPlugin.getInstance().getSession(player);
            final var region = session.getSelection();

            final var group = context.getArgument("group", GroupedArea.class);

            if (!group.getRegion().getRegions().values().iterator().next().getClass().isInstance(region)) {
                plugin.bundle().sendMessage(player, "region.unsupported",
                        Placeholder.parsed("type", region.getClass().getSimpleName()));
                return 0;
            }

            final var success = !group.getRegion().hasRegion(region)
                    && group.getRegion().addRegion(name, region);

            final var message = success ? "area.group.add" : "nothing.changed";
            plugin.bundle().sendMessage(player, message,
                    Placeholder.parsed("area", group.getName()),
                    Placeholder.parsed("region", name));
            return success ? Command.SINGLE_SUCCESS : 0;
        } catch (final IncompleteRegionException ignored) {
            plugin.bundle().sendMessage(player, "region.select");
            return 0;
        }
    }

    private static int delete(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var group = context.getArgument("group", GroupedArea.class);

        if (!plugin.areaService().delete(group)) {
            plugin.bundle().sendMessage(sender, "area.group.delete.failed",
                    Placeholder.parsed("group", group.getName()));
            return 0;
        }

        try {
            final var region = group.getRegion().getRegions().values().iterator().next();
            final var area = plugin.areaService().creator(group)
                    .region(region)
                    .create();
            plugin.bundle().sendMessage(sender, "area.group.delete",
                    Placeholder.parsed("group", area.getName()));
            return Command.SINGLE_SUCCESS;
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to create grouped area", e);
            if (sender instanceof ConsoleCommandSender) return 0;
            plugin.bundle().sendMessage(sender, "command.error");
            return 0;
        }
    }

    private static int create(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var sender = context.getSource().getSender();
        final var area = context.getArgument("area", RegionizedArea.class);

        if (!plugin.areaService().delete((RegionizedArea<?>) area)) {
            plugin.bundle().sendMessage(sender, "area.group.create.failed",
                    Placeholder.parsed("area", area.getName()));
            return 0;
        }

        try {
            final var grouped = plugin.areaService().creator((RegionizedArea<?>) area)
                    .region(new GroupedRegion(Map.of("root", area.getRegion())))
                    .create();

            plugin.bundle().sendMessage(sender, "area.group.create",
                    Placeholder.parsed("area", grouped.getName()));
            return Command.SINGLE_SUCCESS;
        } catch (final Exception e) {
            plugin.getComponentLogger().error("Failed to create grouped area", e);
            if (sender instanceof ConsoleCommandSender) return 0;
            plugin.bundle().sendMessage(sender, "command.error");
            return 0;
        }
    }

    private static int select(final CommandContext<CommandSourceStack> context, final ProtectPlugin plugin) {
        final var player = (Player) context.getSource().getSender();
        final var group = context.getArgument("group", GroupedArea.class);
        final var name = context.getArgument("region", String.class);

        final var region = group.getRegion().getRegion(name);
        if (region == null) {
            plugin.bundle().sendMessage(player, "area.group.region.unknown",
                    Placeholder.parsed("group", group.getName()),
                    Placeholder.parsed("region", name));
            return 0;
        }

        final var session = WorldEditPlugin.getInstance().getSession(player);
        final var selector = new CuboidRegionSelector(null, region.getMinimumPoint(), region.getMaximumPoint());
        session.setRegionSelector(BukkitAdapter.adapt(group.getWorld()), selector);
        session.updateServerCUI(BukkitAdapter.adapt(player));
        plugin.bundle().sendMessage(player, "area.select.group",
                Placeholder.parsed("group", group.getName()),
                Placeholder.parsed("region", name));
        return Command.SINGLE_SUCCESS;
    }
}
