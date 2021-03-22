
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ThreadLocalRandom;

import static Method.Client.Main.setmgr;

public class Derp extends Module {

    /////////////////////

    public Derp() {
        super("Derp", Keyboard.KEY_NONE, Category.MOVEMENT, "Derp");
    }

    Setting Silent = setmgr.add(new Setting("Silent", this, true));
    Setting Yaw = setmgr.add(new Setting("Yaw", this, true));
    Setting Pitch = setmgr.add(new Setting("Pitch", this, true));
    Setting illegal = setmgr.add(new Setting("illegal Range?", this, false));

    @Override
    public void onEnable() {
        if (illegal.getValBoolean())
            ChatUtils.warning("Going beyond max normally possible");
    }

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        int doubleit = illegal.getValBoolean() ? 2 : 1;
        if (!Silent.getValBoolean()) {
            if (Yaw.getValBoolean())
                mc.player.rotationYaw = (float) ThreadLocalRandom.current().nextDouble(-180 * doubleit, 180 * doubleit);
            if (Pitch.getValBoolean())
                mc.player.rotationPitch = (float) ThreadLocalRandom.current().nextDouble(-90 * doubleit, 90 * doubleit);
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        mc.player.rotationYawHead = Utils.random(-180, 180);
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (Silent.getValBoolean()) {
            int doubleit = illegal.getValBoolean() ? 2 : 1;
            if ((packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) && side == Connection.Side.OUT) {
                final CPacketPlayer packet2 = (CPacketPlayer) packet;
                if (Pitch.getValBoolean())
                    packet2.pitch = Utils.random(-180 * doubleit, 180 * doubleit);
                if (Yaw.getValBoolean())
                    packet2.yaw = Utils.random(-90 * doubleit, 90 * doubleit);
            }
        }
        return true;
    }
}
