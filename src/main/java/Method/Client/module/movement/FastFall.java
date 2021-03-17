
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class FastFall extends Module {

    /////////////////////

    Setting speed = setmgr.add(new Setting("Speed", this, .1, .1, 4, false));
    Setting timer = setmgr.add(new Setting("timer", this, false));

    public FastFall() {
        super("FastFall", Keyboard.KEY_NONE, Category.MOVEMENT, "Fast Fall");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (timer.getValBoolean() && mc.player.onGround) {
            setTickLength((float) (50));
        }

        if (mc.player.isElytraFlying() || mc.player.capabilities.isFlying) {
            return;
        }
        final boolean b = !mc.world.isAirBlock(mc.player.getPosition().add(0, -1, 0)) || !mc.world.isAirBlock(mc.player.getPosition().add(0, -2, 0));
        if (!mc.player.onGround && !b) {
            if (timer.getValBoolean() && !mc.player.onGround)
                setTickLength((float) (50 / speed.getValDouble()));
            else
                mc.player.motionY = -this.speed.getValDouble();
        }
    }

    private void setTickLength(float tickLength) {
        mc.timer.tickLength = 1 * tickLength;
    }

    @Override
    public void onDisable() {
        setTickLength(50);
    }

}
