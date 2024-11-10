package dev.klash.bowtie.utility;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtility {

    public static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().connection.sendPacket(packet);
    }
}
