package net.thenextlvl.protect.flag.location;

import com.mojang.brigadier.arguments.ArgumentType;
import core.paper.command.WrappedArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.Location;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class LocationFlag extends Flag<Location> {
    public LocationFlag(@NonNull Key key, @Nullable Location defaultValue, @Nullable Location protectedValue, boolean nullable) {
        super(key, defaultValue, protectedValue, nullable);
    }

    public LocationFlag(@NonNull Key key, @NonNull Location defaultValue, @Nullable Location protectedValue) {
        super(key, defaultValue, protectedValue);
    }

    public LocationFlag(@NonNull Key key, @Nullable Location defaultValue, boolean nullable) {
        super(key, defaultValue, nullable);
    }

    public LocationFlag(@NonNull Key key) {
        super(key);
    }

    @Override
    public @NonNull Class<Location> getValueType() {
        return Location.class;
    }

    @Override
    public @NonNull ArgumentType<Location> getArgumentType() {
        return new WrappedArgumentType<>(
                ArgumentTypes.finePosition(),
                (reader, type) -> {
                    // var resolver = context.getArgument("value", FinePositionResolver.class);
                    // var area = context.getArgument("area", Area.class);
                    // return resolver.resolve(context.getSource()).toLocation(area.getWorld());
                    return null; // fixme
                },
                (context, builder) -> builder.buildFuture());
    }
}
