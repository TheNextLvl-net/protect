package net.thenextlvl.protect.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.command.argument.AreaArgumentType;
import net.thenextlvl.protect.flag.ProtectionFlag;

class AreaProtectCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> create(ProtectPlugin plugin) {
        return Commands.literal("protect")
                .requires(stack -> stack.getSender().hasPermission("protect.command.area.protect"))
                .then(Commands.argument("area", new AreaArgumentType(plugin))
                        .then(Commands.literal("remove")
                                .executes(context -> unprotect(context, plugin)))
                        .executes(context -> protect(context, plugin)));
    }

    private static int unprotect(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var area = context.getArgument("area", Area.class);
        var changes = plugin.flagRegistry().getFlags().stream()
                .filter(flag -> flag instanceof ProtectionFlag<?>)
                .filter(area::removeFlag)
                .count();
        var message = changes > 0 ? "area.unprotected" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("amount", String.valueOf(changes)),
                Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static int protect(CommandContext<CommandSourceStack> context, ProtectPlugin plugin) {
        var area = context.getArgument("area", Area.class);
        var changes = plugin.flagRegistry().getFlags().stream()
                .filter(flag -> flag instanceof ProtectionFlag<?>)
                .map(flag -> (ProtectionFlag<Object>) flag)
                .filter(flag -> area.setFlag(flag, flag.protectedValue()))
                .count();
        var message = changes > 0 ? "area.protected" : "nothing.changed";
        plugin.bundle().sendMessage(context.getSource().getSender(), message,
                Placeholder.parsed("amount", String.valueOf(changes)),
                Placeholder.parsed("area", area.getName()));
        return Command.SINGLE_SUCCESS;
    }
}
