package net.thenextlvl.protect.util;

import core.i18n.file.ResourceMigrator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Set;

@NullMarked
public final class MessageMigrator implements ResourceMigrator {
    private final Set<MigrationRule> rules = Set.of(
            new MigrationRule(Locale.US, "area.list", "<areas>", "<areas:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.list", "<areas>", "<areas:'<gray>, </gray>'>"),

            new MigrationRule(Locale.US, "area.list.global", "<areas>", "<areas:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.list.global", "<areas>", "<areas:'<gray>, </gray>'>"),

            new MigrationRule(Locale.US, "area.info", "<flags>", "<flags:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.info", "<flags>", "<flags:'<gray>, </gray>'>"),

            new MigrationRule(Locale.US, "area.members", "<members>", "<members:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.members", "<members>", "<members:'<gray>, </gray>'>"),

            new MigrationRule(Locale.US, "area.flag.list", "<flags>", "<flags:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.flag.list", "<flags>", "<flags:'<gray>, </gray>'>"),

            new MigrationRule(Locale.US, "area.list.groups", "<groups>", "<groups:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.list.groups", "<groups>", "<groups:'<gray>, </gray>'>"),

            new MigrationRule(Locale.US, "area.list.regions", "<regions>", "<regions:'<gray>, </gray>'>"),
            new MigrationRule(Locale.GERMANY, "area.list.regions", "<regions>", "<regions:'<gray>, </gray>'>")
    );

    @Override
    public @Nullable Migration migrate(Locale locale, String key, String message) {
        return rules.stream()
                .filter(rule -> rule.locale().equals(locale))
                .filter(rule -> rule.key().equals(key))
                .filter(rule -> message.contains(rule.match()))
                .findAny()
                .map(rule -> message.replace(rule.match(), rule.replacement()))
                .map(string -> new Migration(key, string))
                .orElse(null);
    }

    private record MigrationRule(Locale locale, String key, String match, String replacement) {
    }
}
