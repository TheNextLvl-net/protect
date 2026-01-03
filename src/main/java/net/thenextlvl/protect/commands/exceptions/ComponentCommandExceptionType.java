package net.thenextlvl.protect.commands.exceptions;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ComponentCommandExceptionType extends SimpleCommandExceptionType {
    public ComponentCommandExceptionType(Component component) {
        super(MessageComponentSerializer.message().serialize(component));
    }
}
