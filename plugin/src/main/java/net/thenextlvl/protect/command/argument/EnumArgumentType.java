package net.thenextlvl.protect.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import core.paper.command.WrappedArgumentType;

import java.util.Arrays;

public class EnumArgumentType<T extends Enum<T>> extends WrappedArgumentType<String, T> {
    public EnumArgumentType(Class<T> clazz) {
        super(StringArgumentType.string(), (reader, type) -> Arrays.stream(clazz.getEnumConstants())
                .filter(constant -> constant.name().equalsIgnoreCase(type.replace(" ", "_")))
                .findAny().orElseThrow(() -> new IllegalArgumentException(
                        "Unknown enum constant: " + reader.getRemaining()
                )), (context, builder) -> {
            Arrays.stream(clazz.getEnumConstants())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .map(s -> s.replace("_", " "))
                    .filter(s -> s.contains(builder.getRemaining()))
                    .map(StringArgumentType::escapeIfRequired)
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
