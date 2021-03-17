
package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class FastSleep extends Module {

    /////////////////////

    public FastSleep() {
        super("FastSleep", Keyboard.KEY_NONE, Category.MISC, "Fast Sleep");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = mc.player;
        if (player.isPlayerSleeping()) {
            if (player.getSleepTimer() > 10) {
                player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.STOP_SLEEPING));
            }
        }

    }


}
