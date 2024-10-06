package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.area.Area;

import java.util.function.BiPredicate;

public class AreaArgumentType extends WrappedArgumentType<String, Area> {
    public AreaArgumentType(ProtectPlugin plugin) {
        this(plugin, (context, area) -> true);
    }
    public AreaArgumentType(ProtectPlugin plugin, BiPredicate<CommandContext<?>, ? super Area> filter) {
        super(StringArgumentType.string(), (reader, type) -> plugin.areaProvider().getArea(type)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown area: " + type)),
                (context, builder) -> {
                    plugin.areaProvider().getAreas()
                            .filter(area -> filter.test(context, area))
                            .map(Area::getName)
                            .filter(s -> s.contains(builder.getRemaining()))
                            .map(StringArgumentType::escapeIfRequired)
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
