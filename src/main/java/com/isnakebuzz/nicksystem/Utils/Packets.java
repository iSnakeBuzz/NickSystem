package com.isnakebuzz.nicksystem.Utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Packets {

    public static void sendActionBar(Player p, String text) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void respawn(final Player player) {
        final PacketPlayOutRespawn packet = new PacketPlayOutRespawn(((CraftPlayer) player).getWorld().getEnvironment().getId(), EnumDifficulty.getById(((CraftPlayer) player).getWorld().getDifficulty().getValue()), WorldType.getType(((CraftPlayer) player).getWorld().getWorldType().getName()), WorldSettings.EnumGamemode.getById(((CraftPlayer) player).getGameMode().getValue()));
        sendPacket(player, packet);
    }

    public static void sendPacket(final Packet<?> packet) {
        for (final Player all : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static void sendPacket(Player p, final Packet<?> packet) {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void removeFromTab(final Player p) {
        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) p).getHandle());
        sendPacket(packet);
    }

    public static void addToTab(final Player p) {
        final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) p).getHandle());
        sendPacket(packet);
    }

}
