
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class LiquidSpeed extends Module {

    /////////////////////

    Setting waterSpeed = setmgr.add(new Setting("waterSpeed", this, 1, .9, 1.1, false));
    Setting lavaSpeed = setmgr.add(new Setting("lavaSpeed", this, 1, .9, 1.1, false));
    Setting mode = setmgr.add(new Setting("Mode", this, "Vanilla", "Vanilla", "Bypass"));
    private final TimerUtils timer = new TimerUtils();

    public LiquidSpeed() {
        super("LiquidSpeed", Keyboard.KEY_NONE, Category.MOVEMENT, "Liquid Speed");
    }

    ///////////////////

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Bypass")) {
            if (mc.player.isInWater() && this.timer.isDelay((long) 940.0)) {
                mc.player.motionX *= 1.005;
                mc.player.motionZ *= 1.005;
                mc.player.motionY = 0.4;
                this.timer.setLastMS();
            }
        }
        if (mode.getValString().equalsIgnoreCase("Vanilla")) {
            final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY + 0.4, mc.player.posZ);
            if (mc.world.getBlockState(blockPos).getBlock() == Blocks.LAVA) {
                Speed(lavaSpeed);
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionY = 0.06;
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.player.motionY = -0.14;
                }
            }
            if (mc.player.isInWater()) {
                Speed(waterSpeed);
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.player.motionY *= waterSpeed.getValDouble() / 1.2;
                }
            }
        }
    }

    private void Speed(Setting waterSpeed) {
        if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
            mc.player.motionX *= waterSpeed.getValDouble();
            mc.player.motionZ *= waterSpeed.getValDouble();
        }
    }

}
