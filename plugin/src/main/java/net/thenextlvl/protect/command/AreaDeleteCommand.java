package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.RegionizedArea;
import net.thenextlvl.protect.command.argument.RegionizedAreaArgumentType;
import org.jspecify.annotations.NullMarked;

@NullMarked
class AreaDeleteCommand {
    private final ProtectPlugin plugin;

    AreaDeleteCommand(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("delete")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.delete"))
                .then(Commands.argument("area", new RegionizedAreaArgumentType(plugin))
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var area = (RegionizedArea<?>) context.getArgument("area", RegionizedArea.class);

        var message = area.getSchematic().exists()
                ? "area.schematic.delete" : plugin.areaService().delete(area)
                ? "area.delete.success" : "area.delete.failed";

        plugin.bundle().sendMessage(sender, message, Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
