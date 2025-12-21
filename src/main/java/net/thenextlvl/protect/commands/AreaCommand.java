package net.thenextlvl.protect.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.thenextlvl.protect.ProtectPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class AreaCommand {
    public static LiteralCommandNode<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("area")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area"))
                .then(AreaCreateCommand.create(plugin))
                .then(AreaDeleteCommand.create(plugin))
                .then(AreaFlagCommand.create(plugin))
                .then(AreaGroupCommand.create(plugin))
                .then(AreaInfoCommand.create(plugin))
                .then(AreaListCommand.create(plugin))
                .then(AreaMembersCommand.create(plugin))
                .then(AreaOwnerCommand.create(plugin))
                .then(AreaParentCommand.create(plugin))
                .then(AreaPriorityCommand.create(plugin))
                .then(AreaProtectCommand.create(plugin))
                .then(AreaRedefineCommand.create(plugin))
                .then(AreaSchematicCommand.create(plugin))
                .then(AreaSelectCommand.create(plugin))
                .then(AreaTeleportCommand.create(plugin))
                .build();
    }
}
