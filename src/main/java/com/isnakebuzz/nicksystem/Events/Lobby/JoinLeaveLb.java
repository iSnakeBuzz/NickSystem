package com.isnakebuzz.nicksystem.Events.Lobby;

import com.isnakebuzz.nicksystem.Main;
import com.isnakebuzz.nicksystem.Player.NickPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.sql.SQLException;

public class JoinLeaveLb implements Listener {

    private Main plugin;

    public JoinLeaveLb(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerLoginEvent(AsyncPlayerPreLoginEvent e) throws SQLException, IOException {
        plugin.getPlayerBase().createPlayer(e.getUniqueId());
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        NickPlayer nickPlayer = plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
        nickPlayer.setPlayer(e.getPlayer());

        if (e.getPlayer().hasPermission("nick.use")) {
            if (nickPlayer.isHidden()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nte player %player clear".replaceAll("%player", e.getPlayer().getName()));
            }
        }

    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        plugin.getPlayerManager().removePlayer(p.getUniqueId());
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}
