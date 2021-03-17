package Method.Client.utils.Screens.Custom.Packet;

import net.minecraft.network.Packet;

public class AntiPacketPacket {

    public Packet packet;
    public boolean visable;

    public AntiPacketPacket(Packet packet) {
        this.packet = packet;
    }

}
