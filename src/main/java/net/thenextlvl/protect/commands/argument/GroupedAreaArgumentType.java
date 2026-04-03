package net.thenextlvl.protect.commands.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GroupedArea;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@NullMarked
public final class GroupedAreaArgumentType implements CustomArgumentType.Converted<GroupedArea, String> {
    private final ProtectPlugin plugin;
    private final Predicate<? super GroupedArea> filter;

    public GroupedAreaArgumentType(final ProtectPlugin plugin) {
        this(plugin, area -> true);
    }

    public GroupedAreaArgumentType(final ProtectPlugin plugin, final Predicate<? super GroupedArea> filter) {
        this.plugin = plugin;
        this.filter = filter;
    }

    @Override
    public GroupedArea convert(final String nativeType) {
        final var area = plugin.areaProvider().getArea(nativeType).orElseThrow(() ->
                new IllegalArgumentException("Unknown area: " + nativeType));
        if (area instanceof final GroupedArea grouped) return grouped;
        throw new IllegalStateException("Not a grouped area: " + area.getName());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        plugin.areaProvider().getAreas()
                .filter(area -> area instanceof final GroupedArea grouped && filter.test(grouped))
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
