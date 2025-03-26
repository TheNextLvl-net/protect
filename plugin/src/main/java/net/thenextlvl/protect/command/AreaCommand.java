package net.thenextlvl.protect.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.thenextlvl.protect.ProtectPlugin;

public class AreaCommand {
    private final ProtectPlugin plugin;

    public AreaCommand(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                event.registrar().register(Commands.literal("area")
                        .requires(stack -> stack.getSender().hasPermission("protect.command.area"))
                        .then(new AreaCreateCommand(plugin).create())
                        .then(new AreaDeleteCommand(plugin).create())
                        .then(new AreaFlagCommand(plugin).create())
                        .then(new AreaGroupCommand(plugin).create())
                        .then(new AreaInfoCommand(plugin).create())
                        .then(new AreaListCommand(plugin).create())
                        .then(new AreaMembersCommand(plugin).create())
                        .then(new AreaOwnerCommand(plugin).create())
                        .then(new AreaParentCommand(plugin).create())
                        .then(new AreaPriorityCommand(plugin).create())
                        .then(new AreaProtectCommand(plugin).create())
                        .then(new AreaRedefineCommand(plugin).create())
                        .then(new AreaSchematicCommand(plugin).create())
                        .then(new AreaSelectCommand(plugin).create())
                        .then(new AreaTeleportCommand(plugin).create())
                        .build())));
    }
}
