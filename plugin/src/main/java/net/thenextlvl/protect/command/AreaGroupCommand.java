package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.GroupedArea;
import net.thenextlvl.protect.command.argument.GroupedAreaArgumentType;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class AreaGroupCommand {
    private final ProtectPlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("group")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.group"))
                .then(Commands.literal("create")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.create"))
                        .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin,
                                        regionizedArea -> !(regionizedArea instanceof GroupedArea)))
                                .executes(this::create)))
                .then(Commands.literal("add")
                        .requires(stack -> stack.getSender() instanceof Player player
                                           && player.hasPermission("protect.command.area.group.add"))
                        .then(Commands.argument("area", new GroupedAreaArgumentType(plugin))
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            var name = context.getArgument("name", String.class);
                                            return add(context, name);
                                        }))
                                .executes(context -> {
                                    var area = context.getArgument("area", GroupedArea.class);
                                    var name = "region-" + (area.getRegion().getRegions().size() + 1);
                                    while (area.getRegion().hasRegion(name)) name = name + ".1";
                                    return add(context, name);
                                })))
                .then(Commands.literal("inspect")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.inspect"))
                        .then(Commands.argument("area", new GroupedAreaArgumentType(plugin))
                                .executes(this::inspect)))
                .then(Commands.literal("remove")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area.group.remove"))
                        .then(Commands.argument("area", new GroupedAreaArgumentType(plugin))
                                .then(Commands.argument("region", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            var group = context.getChild().getArgument("area", GroupedArea.class);
                                            group.getRegion().getRegions().keySet().forEach(builder::suggest);
                                            return builder.buildFuture();
                                        }).executes(this::remove))));
    }

    private int remove(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int inspect(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int add(CommandContext<CommandSourceStack> context, String name) {
        return Command.SINGLE_SUCCESS;
    }

    private int create(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }
}
