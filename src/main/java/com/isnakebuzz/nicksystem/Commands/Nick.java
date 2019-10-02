package com.isnakebuzz.nicksystem.Commands;

import com.isnakebuzz.nicksystem.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Nick implements CommandExecutor {

    private Main plugin;

    public Nick(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Configuration config = plugin.getConfigUtils().getConfig(plugin, "Settings");

        if (cmd.getName().equalsIgnoreCase("nick") && sender.hasPermission("nick.use")) {
            if (args.length < 1) {
                Player p = (Player) sender;

                return true;
            }

            Player p = (Player) sender;
            String nick = args[0];


        }
        return false;
    }

    private String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', c);
    }

}
