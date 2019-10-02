package com.isnakebuzz.nicksystem.Configurations;

import java.io.File;
import org.bukkit.plugin.Plugin;

public class ConfigCreator {
	
	private static ConfigCreator instance;
	private File pluginDir;
	private File configFile;
  
	public void setup(Plugin p, String configname){
	    this.pluginDir = p.getDataFolder();
	    this.configFile = new File(this.pluginDir, configname+".yml");
		if (!this.configFile.exists()) {
			p.saveResource(configname+".yml", true);
		}
	}
  
	public static ConfigCreator get(){
		if (instance == null) {
			instance = new ConfigCreator();
		}
		return instance;
	}
}