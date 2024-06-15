package live.deact.sunnycredits.listeners;

import live.deact.sunnycredits.SunnyCredits;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final SunnyCredits plugin;

    public PlayerJoinListener(SunnyCredits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!plugin.getDatabaseManager().isPlayerRegistered(uuid)) {
            plugin.getDatabaseManager().registerPlayer(uuid);
        }
    }
}