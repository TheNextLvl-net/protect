package net.thenextlvl.protect.command;

import cloud.commandframework.arguments.StandardCommandSyntaxFormatter;

public class CustomSyntaxFormatter<C> extends StandardCommandSyntaxFormatter<C> {
    @Override
    protected StandardCommandSyntaxFormatter.FormattingInstance createInstance() {
        return new StandardCommandSyntaxFormatter.FormattingInstance() {
            @Override
            public String getOptionalPrefix() {
                return "<dark_gray>(<gold>";
            }

            @Override
            public String getOptionalSuffix() {
                return "<dark_gray>)";
            }

            @Override
            public String getRequiredPrefix() {
                return "<dark_gray>[<gold>";
            }

            @Override
            public String getRequiredSuffix() {
                return "<dark_gray>]";
            }

            @Override
            public void appendPipe() {
                appendName(" <dark_gray>|<red> ");
            }
        };
    }
}
