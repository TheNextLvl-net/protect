package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
public final class FlagProviderArgumentType implements CustomArgumentType.Converted<Plugin, String> {
    private final ProtectPlugin plugin;

    public FlagProviderArgumentType(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin convert(String nativeType) {
        var provider = plugin.getServer().getPluginManager().getPlugin(nativeType);
        if (provider != null) return provider;
        throw new IllegalArgumentException("Unknown provider: " + nativeType);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.flagRegistry().getRegistry().keySet().stream()
                .map(Plugin::getName)
                .filter(s -> s.contains(builder.getRemaining()))
                .map(StringArgumentType::escapeIfRequired)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
