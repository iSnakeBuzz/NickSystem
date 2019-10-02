package com.isnakebuzz.nicksystem.Utils.Managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isnakebuzz.nicksystem.Main;
import com.isnakebuzz.nicksystem.Skins.SkinData;
import com.isnakebuzz.nicksystem.Utils.Calls.Callback;
import com.isnakebuzz.nicksystem.Utils.RandomUtils;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class SkinManager {

    private Main plugin;

    public SkinManager(Main plugin) {
        this.plugin = plugin;
    }

    public void getSkin(String playerName, String skinID, Callback<SkinData> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            JsonObject jsonresponse = getJsonResponse("https://api.mineskin.org/get/id/" + skinID);
            if (jsonresponse != null && !jsonresponse.has("error")) {
                JsonObject textureProperty = jsonresponse.getAsJsonObject("data").getAsJsonObject("texture");
                String value = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();

                SkinData skinData = new SkinData(playerName, value, signature);
                callback.done(skinData);
            }
        });
    }

    public void getSkin(String playerName, int skinID, Callback<SkinData> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            JsonObject jsonresponse = getJsonResponse("https://api.mineskin.org/get/id/" + skinID);
            if (jsonresponse != null && !jsonresponse.has("error")) {
                JsonObject textureProperty = jsonresponse.getAsJsonObject("data").getAsJsonObject("texture");
                String value = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();

                SkinData skinData = new SkinData(playerName, value, signature);
                callback.done(skinData);
            }
        });
    }

    public void generateRandom(Callback<SkinData> callback) {
        int randomSkin = 1000 + (int) (Math.random() * 45000);
        getSkin(RandomUtils.generateName(), randomSkin, callback);
    }

    public void generateNormal(String playerName, Callback<SkinData> callback) {
        int randomSkin = 1000 + (int) (Math.random() * 45000);
        getSkin(playerName, randomSkin, callback);
    }

    private JsonObject getJsonResponse(String url) {
        URL ipAdress;
        JsonObject rootobj = null;
        try {
            ipAdress = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(ipAdress.openStream()));
            String jsonresponse = in.readLine();
            JsonParser jsonParser = new JsonParser();
            JsonElement root = jsonParser.parse(jsonresponse);
            rootobj = root.getAsJsonObject();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return rootobj;
    }

}
