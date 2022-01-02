package lol.maltest.arenasystem.util;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

public class TitleAPI {

    private PacketPlayOutTitle title;
    private PacketPlayOutTitle subtitle;

    public TitleAPI(String text) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text.replace('&', '§') + "\"}"), 20, 40, 20);
        this.title = title;
    }

    public void SubTitle(String text2) {
        PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text2.replace('&', '§') + "\"}"), 20, 40, 20);
        this.subtitle = subtitle;
    }

    public void sendToPlayer(Player p) {
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(subtitle);
    }

    public void sendToAll() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(subtitle);
        }
    }
}
