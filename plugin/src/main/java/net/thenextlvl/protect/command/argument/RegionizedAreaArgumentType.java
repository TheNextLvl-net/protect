package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.RegionizedArea;

import java.util.function.Predicate;

public class RegionizedAreaArgumentType extends WrappedArgumentType<String, RegionizedArea<?>> {
    public RegionizedAreaArgumentType(ProtectPlugin plugin) {
        this(plugin, area -> true);
    }

    public RegionizedAreaArgumentType(ProtectPlugin plugin, Predicate<? super RegionizedArea<?>> filter) {
        super(StringArgumentType.string(), (reader, type) -> {
            var area = plugin.areaProvider().getArea(type).orElseThrow(() ->
                    new IllegalArgumentException("Unknown area: " + type));
            if (area instanceof RegionizedArea<?> regionized) return regionized;
            throw new IllegalStateException("Not a regionized area: " + area.getName());
        }, (context, builder) -> {
            plugin.areaProvider().getAreas()
                    .filter(area -> area instanceof RegionizedArea<?> regionized && filter.test(regionized))
                    .map(Area::getName)
                    .filter(s -> s.contains(builder.getRemaining()))
                    .map(StringArgumentType::escapeIfRequired)
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
