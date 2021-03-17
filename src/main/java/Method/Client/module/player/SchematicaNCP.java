package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class SchematicaNCP extends Module {


    public SchematicaNCP() {
        super("PrinterBypass", Keyboard.KEY_NONE, Category.PLAYER, "PrinterBypass");
    }

    private final TimerUtils timer = new TimerUtils();
    Setting KeepRotation = setmgr.add(new Setting("Keep Rotation", this, true));

    public float[] Rots;

    @Override
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        timer.setLastMS();
        final float[] array = Utils.getNeededRotations(event.getHitVec(), 0, 0);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(array[0], array[1], mc.player.onGround));
        Rots = array;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT)
            if (KeepRotation.getValBoolean() && Rots != null)
                if ((packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation)) {
                    if (!timer.isDelay(4000)) {
                        final CPacketPlayer packet2 = (CPacketPlayer) packet;
                        packet2.yaw = Rots[0];
                        packet2.pitch = Rots[1];
                    }
                }
        return true;
    }

}
