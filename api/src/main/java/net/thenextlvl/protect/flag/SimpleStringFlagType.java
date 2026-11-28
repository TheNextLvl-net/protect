package net.thenextlvl.protect.flag;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.thenextlvl.nbt.tag.StringTag;
import net.thenextlvl.nbt.tag.Tag;

record SimpleStringFlagType(StringType kind) implements StringFlagType {
    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public Tag encode(final String value) {
        return StringTag.of(value);
    }

    @Override
    public String decode(final Tag tag) {
        return tag.getAsString();
    }

    @Override
    public ArgumentType<String> argumentType() {
        return switch (kind) {
            case SINGLE_WORD -> StringArgumentType.word();
            case QUOTABLE_PHRASE -> StringArgumentType.string();
            case GREEDY_PHRASE -> StringArgumentType.greedyString();
        };
    }
}
