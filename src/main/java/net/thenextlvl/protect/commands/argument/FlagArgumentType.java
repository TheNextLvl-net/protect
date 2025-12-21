package net.thenextlvl.protect.commands.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@NullMarked
public final class FlagArgumentType implements CustomArgumentType.Converted<Flag<?>, Key> {
    private final ProtectPlugin plugin;
    private final Predicate<? super Flag<?>> filter;

    public FlagArgumentType(ProtectPlugin plugin) {
        this(plugin, flag -> true);
    }

    public FlagArgumentType(ProtectPlugin plugin, Predicate<? super Flag<?>> filter) {
        this.plugin = plugin;
        this.filter = filter;
    }

    @Override
    public Flag<?> convert(Key nativeType) {
        return plugin.flagRegistry().getFlag(nativeType).filter(filter).orElseThrow(() ->
                new IllegalArgumentException("Unknown flag: " + nativeType));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.flagRegistry().getFlags().stream()
                .filter(filter)
                .map(Flag::key)
                .map(Key::asString)
                .filter(s -> s.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<Key> getNativeType() {
        return ArgumentTypes.key();
    }
}
