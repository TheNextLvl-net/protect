package net.thenextlvl.protect.version;

import core.paper.version.PaperHangarVersionChecker;
import core.version.SemanticVersion;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class PluginVersionChecker extends PaperHangarVersionChecker<SemanticVersion> {
    public PluginVersionChecker(Plugin plugin) {
        super(plugin, "TheNextLvl", "Protect");
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
