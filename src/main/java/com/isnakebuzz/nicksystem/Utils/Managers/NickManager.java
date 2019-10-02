package com.isnakebuzz.nicksystem.Utils.Managers;

import com.isnakebuzz.nicksystem.Main;
import com.isnakebuzz.nicksystem.Player.NickPlayer;
import com.isnakebuzz.nicksystem.Skins.SkinData;
import com.isnakebuzz.nicksystem.Utils.Packets;
import com.isnakebuzz.nicksystem.Utils.Profile.GameProfileBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

import static com.isnakebuzz.nicksystem.Utils.Packets.sendPacket;

public class NickManager {

    private Main plugin;

    public NickManager(Main plugin) {
        this.plugin = plugin;
    }

    public void hidePlayer(Player p) {
        CraftPlayer cp = (CraftPlayer) p;
        NickPlayer nickPlayer = plugin.getPlayerManager().getPlayer(p.getUniqueId());

        String nickName = nickPlayer.getSkinData().getName();


        try {
            setSkinPlayer(cp, nickPlayer.getSkinData());
        } catch (Exception ex) {
            originalSkin(cp);
        }

        setProfile(p, nickName);
        Field nameField = getNameField();

        try {
            assert nameField != null;
            nameField.set(cp.getProfile(), nickName);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        //Sending data to server
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(cp.getEntityId());

        sendPacket(destroy);
        Packets.removeFromTab(cp);
        Packets.addToTab(cp);
        Packets.respawn(p);
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(cp.getHandle());
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.equals(p)) {
                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(spawn);
            }
        }

    }

    private void setSkinPlayer(CraftPlayer cp, SkinData skinData) {
        GameProfile profile = cp.getProfile();
        String value = skinData.getValue();
        String signature = skinData.getSignature();
        if (profile.getProperties().containsKey("textures")) {
            profile.getProperties().removeAll("textures");
        }
        profile.getProperties().put("textures", new Property("textures", value, signature));
    }

    private void originalSkin(CraftPlayer cp) {
        GameProfile skimp;
        UUID uuid = UUID.fromString("a25ed08d-5d66-4d14-89f9-6cae6e190369");
        try {
            skimp = GameProfileBuilder.fetch(uuid);
        } catch (IOException e) {
            cp.sendMessage("§cError al cargar la skin!");
            e.printStackTrace();
            return;
        }
        Collection<Property> prop = skimp.getProperties().get("textures");
        cp.getProfile().getProperties().removeAll("textures");
        cp.getProfile().getProperties().putAll("textures", prop);
    }

    private void setProfile(Player p, String Name) {
        PacketPlayOutPlayerInfo player = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ((CraftPlayer) p).getHandle());
        for (Player all : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(player);
        }

        try {
            //Getting game profile
            GameProfile profile = ((CraftPlayer) p).getProfile();

            //Getting name field
            Field nameField = profile.getClass().getDeclaredField("name");
            nameField.setAccessible(true);

            //Modifing class
            Field modifierField = nameField.getClass().getDeclaredField("modifiers");
            int modifiers = nameField.getModifiers() & 0x10;
            modifierField.setAccessible(true);
            modifierField.setInt(nameField, modifiers);
            nameField.set(profile, Name);

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        //p.setDisplayName(Name);
    }

    private Field getNameField() {
        try {
            Field field = GameProfile.class.getDeclaredField("name");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
