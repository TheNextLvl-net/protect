package net.thenextlvl.protect.version;

import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.paper.PaperModrinthVersionChecker;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(final Plugin plugin) {
        super(plugin, "YNoH2pBx");
    }

    @Override
    public SemanticVersion parseVersion(final String version) {
        return SemanticVersion.parse(version);
    }
}
