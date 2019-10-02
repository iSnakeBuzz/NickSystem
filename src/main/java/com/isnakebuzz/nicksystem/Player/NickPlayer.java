package com.isnakebuzz.nicksystem.Player;

import com.isnakebuzz.nicksystem.Skins.SkinData;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class NickPlayer {

    private UUID uuid;
    private Player player;
    private SkinData skinData;
    private boolean hidden;

    public NickPlayer(UUID uuid, SkinData skinData, boolean hidden) {
        this.uuid = uuid;
        this.hidden = hidden;
        this.skinData = skinData;
    }

}
