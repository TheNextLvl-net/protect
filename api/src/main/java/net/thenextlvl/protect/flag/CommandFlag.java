package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public interface CommandFlag<T> {
    default boolean supportsSet() {
        return true;
    }

    default boolean supportsAdd() {
        return false;
    }

    default boolean supportsRemove() {
        return false;
    }

    ArgumentType<T> argumentType();

    T resolve(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;

    default <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return builder.buildFuture();
    }
}
