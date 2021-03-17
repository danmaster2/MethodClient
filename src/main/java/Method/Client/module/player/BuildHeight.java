package Method.Client.module.player;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

public class BuildHeight extends Module {
    public BuildHeight() {
        super("BuildHeight", Keyboard.KEY_NONE, Category.PLAYER, "Interact at Build Height");
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
            if (side == Connection.Side.OUT) {
                if(packet instanceof CPacketPlayerTryUseItemOnBlock) {
                    final CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock) packet;
                    if(packet2.getPos().getY() >= 255 && packet2.getDirection() == EnumFacing.UP) {
                        packet2.placedBlockDirection = EnumFacing.DOWN;
                    }
                }
            }
        return true;
    }
}
