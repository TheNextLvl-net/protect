package net.thenextlvl.protect.command;

import org.incendo.cloud.CommandManager;
import org.incendo.cloud.syntax.StandardCommandSyntaxFormatter;

public class CustomSyntaxFormatter<C> extends StandardCommandSyntaxFormatter<C> {
    public CustomSyntaxFormatter(CommandManager<C> manager) {
        super(manager);
    }

    @Override
    protected StandardCommandSyntaxFormatter.FormattingInstance createInstance() {
        return new StandardCommandSyntaxFormatter.FormattingInstance() {
            @Override
            public String optionalPrefix() {
                return "(";
            }

            @Override
            public String optionalSuffix() {
                return ")";
            }

            @Override
            public String requiredPrefix() {
                return "[";
            }

            @Override
            public String requiredSuffix() {
                return "]";
            }

            @Override
            public void appendPipe() {
                appendName(" | ");
            }
        };
    }
}
