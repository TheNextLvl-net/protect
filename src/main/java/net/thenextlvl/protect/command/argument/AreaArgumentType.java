package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

@NullMarked
public final class AreaArgumentType implements CustomArgumentType.Converted<Area, String> {
    private final ProtectPlugin plugin;
    private final BiPredicate<? super CommandContext<?>, ? super Area> filter;

    public AreaArgumentType(ProtectPlugin plugin) {
        this(plugin, (context, area) -> true);
    }

    public AreaArgumentType(ProtectPlugin plugin, BiPredicate<CommandContext<?>, ? super Area> filter) {
        this.plugin = plugin;
        this.filter = filter;
    }

    @Override
    public Area convert(String nativeType) {
        return plugin.areaProvider().getArea(nativeType).orElseThrow(() ->
                new IllegalArgumentException("Unknown area: " + nativeType));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.areaProvider().getAreas()
                .filter(area -> filter.test(context, area))
                .map(Area::getName)
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
