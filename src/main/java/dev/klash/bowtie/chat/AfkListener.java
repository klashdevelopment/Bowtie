package dev.klash.bowtie.chat;

import dev.klash.bowtie.utility.AFKManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AfkListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if(event.hasChangedBlock()) {
            AFKManager.removeIfAFK(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        AFKManager.removeIfAFK(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        AFKManager.remove(event.getPlayer().getUniqueId());
    }
}
