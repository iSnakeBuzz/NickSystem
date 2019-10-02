package com.isnakebuzz.nicksystem.Database;

import com.isnakebuzz.nicksystem.Database.Core.MySQL;
import com.isnakebuzz.nicksystem.Main;
import com.isnakebuzz.nicksystem.Player.NickPlayer;
import com.isnakebuzz.nicksystem.Skins.SkinData;
import com.isnakebuzz.nicksystem.Utils.Base64Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerBase {

    private Main plugin;

    public PlayerBase(Main Main) {
        this.plugin = Main;
    }

    public void createPlayer(UUID p) throws SQLException, IOException {
        if (!playerExists(p)) {
            MySQL.update("INSERT INTO NickSystem (UUID, SkinData, Enabled) VALUES ('" + p.toString() + "', 'none', 'false');");
            NickPlayer nickPlayer = new NickPlayer(p, null, false);
            plugin.getPlayerManager().addPlayer(p, nickPlayer);
        } else {
            NickPlayer nickPlayer = new NickPlayer(p, null, false);
            ResultSet resultSet = MySQL.query("SELECT * FROM NickSystem WHERE UUID='" + p.toString() + "'");
            assert resultSet != null;
            if (resultSet.next()) {
                nickPlayer.setHidden(resultSet.getBoolean("Enabled"));

                String skinData_BASE64 = resultSet.getString("SkinData");

                if (!skinData_BASE64.equals("none")) {
                    SkinData skinData = (SkinData) Base64Utils.fromBase64(skinData_BASE64);
                    nickPlayer.setSkinData(skinData);
                    plugin.log("Debug", skinData.toString());
                }

            }
            plugin.getPlayerManager().addPlayer(p, nickPlayer);
        }
    }

    public void savePlayer(UUID p) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (playerExists(p)) {
                NickPlayer nickPlayer = plugin.getPlayerManager().getPlayer(p);

                String skinData = Base64Utils.toBase64(nickPlayer.getSkinData());

                MySQL.update("UPDATE NickSystem SET " +
                        "SkinData='" + skinData + "', " +
                        "Enabled='" + nickPlayer.isHidden() + "' " +
                        "WHERE UUID='" + p.toString() + "'"
                );
            }
        });
    }

    public boolean playerExists(UUID p) {
        try {
            ResultSet rs = MySQL.query("SELECT * FROM NickSystem WHERE UUID='" + p.toString() + "'");
            return (rs != null && rs.next()) && rs.getString("UUID") != null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void loadMySQL() {
        FileConfiguration config = plugin.getConfigUtils().getConfig(plugin, "Settings");
        MySQL.host = config.getString("MySQL.hostname");
        MySQL.port = config.getInt("MySQL.port");
        MySQL.database = config.getString("MySQL.database");
        MySQL.username = config.getString("MySQL.username");
        MySQL.password = config.getString("MySQL.password");
        MySQL.isEnabled = config.getBoolean("MySQL.enabled");

        MySQL.connect(plugin);
        MySQL.update("CREATE TABLE IF NOT EXISTS NickSystem (UUID VARCHAR(100), SkinData text, Enabled text)");
    }

}
