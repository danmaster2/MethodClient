
package Method.Client.module.movement;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AntiLagback extends Module {

    /////////////////////
    private final List<Object> packets = new CopyOnWriteArrayList<>();
    private boolean sending;
    private int delay;

    public AntiLagback() {
        super("AntiLagback", Keyboard.KEY_NONE, Category.MOVEMENT, "Try Anti Lagback");
    }

    ///////////////////
    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        delay += 1;
        if (delay >= 1) {
            sending = true;
            sendPackets();
            delay = 0;
        }
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (sending) {
            return false;
        }
        boolean input = (mc.gameSettings.keyBindForward.isPressed()) || (mc.gameSettings.keyBindBack.isPressed()) || (mc.gameSettings.keyBindRight.isPressed()) || (mc.gameSettings.keyBindLeft.isPressed());
        if (input && (packet instanceof CPacketPlayer)) {
            packets.add(packet);
        }
        if (packet instanceof CPacketUseEntity) {
            packets.add(packet);
            mc.player.rotationYaw -= 180.0F;
        }
        return !(packet instanceof CPacketPlayer) && !(packet instanceof CPacketPlayerTryUseItemOnBlock) && !(packet instanceof CPacketPlayerDigging) && !(packet instanceof CPacketUseEntity);
    }


    public void sendPackets() {
        if (packets.size() > 0) {
            for (Object packet : packets) {
                if (((packet instanceof CPacketUseEntity)) || ((packet instanceof CPacketPlayerTryUseItemOnBlock)) || ((packet instanceof CPacketPlayerDigging))) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                mc.player.connection.sendPacket((Packet<?>) packet);
            }
        }
        packets.clear();
        sending = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        sending = true;
        sendPackets();
    }
}
