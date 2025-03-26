package net.thenextlvl.protect.command.argument;

import core.paper.command.WrappedArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.key.Key;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;

import java.util.function.Predicate;

public class FlagArgumentType extends WrappedArgumentType<Key, Flag<?>> {
    public FlagArgumentType(ProtectPlugin plugin) {
        this(plugin, flag -> true);
    }

    public FlagArgumentType(ProtectPlugin plugin, Predicate<? super Flag<?>> filter) {
        super(ArgumentTypes.key(), (reader, type) -> plugin.flagRegistry().getFlag(type)
                        .filter(filter).orElseThrow(() -> new IllegalArgumentException("Unknown flag: " + type)),
                (context, builder) -> {
                    plugin.flagRegistry().getFlags().stream()
                            .filter(filter)
                            .map(Flag::key)
                            .map(Key::asString)
                            .filter(s -> s.contains(builder.getRemaining()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
