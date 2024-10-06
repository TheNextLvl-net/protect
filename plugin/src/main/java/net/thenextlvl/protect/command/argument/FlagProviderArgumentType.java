package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.plugin.Plugin;

public class FlagProviderArgumentType extends WrappedArgumentType<String, Plugin> {
    public FlagProviderArgumentType(ProtectPlugin plugin) {
        super(StringArgumentType.string(), (reader, type) -> {
            var provider = plugin.getServer().getPluginManager().getPlugin(type);
            if (provider != null) return provider;
            throw new IllegalArgumentException("Unknown provider: " + type);
        }, (context, builder) -> {
            plugin.flagRegistry().getRegistry().keySet().stream()
                    .map(Plugin::getName)
                    .filter(s -> s.contains(builder.getRemaining()))
                    .map(StringArgumentType::escapeIfRequired)
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
