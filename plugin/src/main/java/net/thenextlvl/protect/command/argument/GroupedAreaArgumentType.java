package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.GroupedArea;

import java.util.function.Predicate;

public class GroupedAreaArgumentType extends WrappedArgumentType<String, GroupedArea> {
    public GroupedAreaArgumentType(ProtectPlugin plugin) {
        this(plugin, area -> true);
    }

    public GroupedAreaArgumentType(ProtectPlugin plugin, Predicate<? super GroupedArea> filter) {
        super(StringArgumentType.string(), (reader, type) -> {
            var area = plugin.areaProvider().getArea(type).orElseThrow(() ->
                    new IllegalArgumentException("Unknown area: " + type));
            if (area instanceof GroupedArea grouped) return grouped;
            throw new IllegalStateException("Not a grouped area: " + area.getName());
        }, (context, builder) -> {
            plugin.areaProvider().getAreas()
                    .filter(area -> area instanceof GroupedArea grouped && filter.test(grouped))
                    .map(Area::getName)
                    .filter(s -> s.contains(builder.getRemaining()))
                    .map(StringArgumentType::escapeIfRequired)
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
