package net.thenextlvl.protect.listener;

import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ConnectionListener implements Listener {
    private final ProtectPlugin plugin;

    public ConnectionListener(ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var area = plugin.areaProvider().getArea(event.getPlayer());
        var collides = area.getFlag(plugin.flags.collisions);
        plugin.collisionController().setCollidable(event.getPlayer(), collides);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.collisionController().remove(event.getPlayer());
    }
}
