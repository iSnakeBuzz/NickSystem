package com.isnakebuzz.nicksystem.Utils.Managers;

import com.isnakebuzz.nicksystem.Main;
import com.isnakebuzz.nicksystem.Player.NickPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private Main plugin;
    private Map<UUID, NickPlayer> playerDataMap;

    public PlayerManager(Main plugin) {
        this.plugin = plugin;

        this.playerDataMap = new HashMap<>();
    }

    public void addPlayer(UUID p, NickPlayer nickPlayer) {
        if (!this.playerDataMap.containsKey(p)) {
            this.playerDataMap.put(p, nickPlayer);
        }
    }

    public NickPlayer getPlayer(UUID p) {
        return this.playerDataMap.getOrDefault(p, null);
    }

    public void removePlayer(UUID p) {
        this.playerDataMap.remove(p);
    }

    public boolean containsPlayer(UUID p) {
        return this.playerDataMap.containsKey(p);
    }

    public Collection<NickPlayer> getValues() {
        return this.playerDataMap.values();
    }

}
