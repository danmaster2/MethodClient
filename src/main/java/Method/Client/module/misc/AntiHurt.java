
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class AntiHurt extends Module {

    /////////////////////
    Setting mode = setmgr.add(new Setting("Mode", this, "Packet", "Death", "Packet"));

    public AntiHurt() {
        super("AntiHurt", Keyboard.KEY_NONE, Category.MISC, "Anti Hurt on some instance");
    }

    @Override
    public void onEnable() {
        if (mode.getValString().equalsIgnoreCase("Death")) {
            if (mc.player != null) {
                mc.player.isDead = true;
                mc.gameSettings.keyBindForward.isPressed();
            }
        }
    }

    @Override
    public void onDisable() {
        mc.player.isDead = false;
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mode.getValString().equalsIgnoreCase("Packet")) {
            return !(packet instanceof SPacketUpdateHealth);
        }
        return true;
    }

}
