package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;

public class FastLadder extends Module {
    Setting mode=setmgr.add(new Setting("Mode", this, "Simple", "Simple","DOWN","Skip"));

    public FastLadder() {
        super("FastLadder", Keyboard.KEY_NONE, Category.PLAYER, "Climb Faster");
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (!mc.player.isOnLadder() || mc.player.moveForward == 0 && mc.player.moveStrafing == 0) {
            return;
        }
        if (mode.getValString().equalsIgnoreCase("Skip")) {
            if (mc.player.isOnLadder()) {
                mc.player.setSprinting(true);
                mc.player.onGround = true;
            }
        }
        if (mode.getValString().equalsIgnoreCase("DOWN")) {
            while (mc.player.isOnLadder() && mc.player.motionY != 0.0D) {
                mc.player.setPosition(mc.player.posX, mc.player.posY + (double) (mc.player.motionY > 0.0D ? 1 : -1), mc.player.posZ);
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
            }
        }
        if (mode.getValString().equalsIgnoreCase("simple"))
            mc.player.motionY = 0.16999999433755875D;
        super.onClientTick(event);
    }


}
