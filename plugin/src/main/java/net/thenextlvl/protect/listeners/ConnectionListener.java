package net.thenextlvl.protect.listeners;

import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ConnectionListener implements Listener {
    private final ProtectPlugin plugin;

    public ConnectionListener(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final var area = plugin.areaProvider().getArea(event.getPlayer());
        final var collides = area.getFlag(plugin.flags.collisions);
        plugin.collisionController().setCollidable(event.getPlayer(), collides);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        plugin.collisionController().remove(event.getPlayer());
    }
}
