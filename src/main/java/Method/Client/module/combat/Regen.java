package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Regen extends Module {


    public Regen() {
        super("Regen", Keyboard.KEY_NONE, Category.COMBAT, "Regen");
    }

    Setting mode = setmgr.add(new Setting("Regen Mode", this, "Vanilla", "Vanilla", "Packet"));
    Setting packets = setmgr.add(new Setting("packets", this, 20, 20, 200, false, mode, "Packet", 2));

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Packet")) {
            if (mc.player.getHealth() < mc.player.getMaxHealth()
                    && mc.player.getFoodStats().getFoodLevel() > 1) {
                for (int i = 0; i < packets.getValDouble(); ++i) {
                    mc.player.connection.sendPacket(new CPacketPlayer());
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("Vanilla")) {
            if (mc.player.getHealth() < 20) {
                mc.timer.tickLength = 0.8f;
                mc.player.setHealth(20);
            }
        }
        super.onClientTick(event);
    }

    @Override
    public void onDisable() {
        if (mode.getValString().equalsIgnoreCase("Vanilla"))
            mc.timer.tickLength = 1f;
    }

}
