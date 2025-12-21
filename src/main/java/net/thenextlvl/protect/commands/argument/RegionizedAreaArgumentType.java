package net.thenextlvl.protect.commands.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@NullMarked
public final class RegionizedAreaArgumentType implements CustomArgumentType.Converted<RegionizedArea<?>, String> {
    private final ProtectPlugin plugin;
    private final Predicate<? super RegionizedArea<?>> filter;

    public RegionizedAreaArgumentType(ProtectPlugin plugin) {
        this(plugin, area -> true);
    }

    public RegionizedAreaArgumentType(ProtectPlugin plugin, Predicate<? super RegionizedArea<?>> filter) {
        this.plugin = plugin;
        this.filter = filter;
    }

    @Override
    public RegionizedArea<?> convert(String nativeType) {
        var area = plugin.areaProvider().getArea(nativeType).orElseThrow(() ->
                new IllegalArgumentException("Unknown area: " + nativeType));
        if (area instanceof RegionizedArea<?> regionized) return regionized;
        throw new IllegalStateException("Not a regionized area: " + area.getName());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        plugin.areaProvider().getAreas()
                .filter(area -> area instanceof RegionizedArea<?> regionized && filter.test(regionized))
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
