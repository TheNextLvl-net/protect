package net.thenextlvl.protect.command.argument;

import core.paper.command.WrappedArgumentType;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.thenextlvl.protect.ProtectPlugin;
import net.thenextlvl.protect.flag.Flag;
import org.bukkit.NamespacedKey;

import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class FlagArgumentType extends WrappedArgumentType<NamespacedKey, Flag<?>> {
    public FlagArgumentType(ProtectPlugin plugin) {
        this(plugin, flag -> true);
    }

    public FlagArgumentType(ProtectPlugin plugin, Predicate<? super Flag<?>> filter) {
        super(ArgumentTypes.namespacedKey(), (reader, type) -> plugin.flagRegistry().getFlag(type)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown flag: " + type)),
                (context, builder) -> {
                    plugin.flagRegistry().getFlags().stream()
                            .filter(filter)
                            .map(Flag::key)
                            .map(NamespacedKey::asString)
                            .filter(s -> s.contains(builder.getRemaining()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }
}
