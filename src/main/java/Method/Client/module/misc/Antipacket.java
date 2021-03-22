
package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Screens.Custom.Packet.AntiPacketGui;
import Method.Client.utils.Screens.Custom.Packet.AntiPacketPacket;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketSpectate;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Antipacket extends Module {

    /////////////////////

    public Antipacket() {
        super("Antipacket", Keyboard.KEY_NONE, Category.MISC, "Cancel Packets");
    }

    Setting Gui = setmgr.add(new Setting("Gui", this, Main.AntiPacketgui));



    @Override
    public boolean onPacket(Object packet, Connection.Side side) {

        System.out.println(packet.toString());
        for (AntiPacketPacket packet2 : AntiPacketGui.GetPackets()) {
            if (packet.getClass().isInstance(packet2.packet)) {
                return false;
            }
        }
        return true;
    }
}
