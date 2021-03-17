package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;


public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Keyboard.KEY_NONE, Category.MOVEMENT, "Always be running");
    }

    Setting Backwards = setmgr.add(new Setting("Backwards", this, false));
    Setting Foodcheck = setmgr.add(new Setting("Food check", this, true));
    Setting ObstacleCheck = setmgr.add(new Setting("Obstical Check", this, true));
    Setting InstarunTimer = setmgr.add(new Setting("InstarunTimer", this, false, ObstacleCheck, 3));
    Setting Fastspeedup = setmgr.add(new Setting("Fastspeedup", this, 10, 2, 40, true, ObstacleCheck, 4));
    double quicktimerrun = 10;
    boolean startedquickrun = false;


    private void setTickLength(float tickLength) {
        mc.timer.tickLength = 1 * tickLength;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player.getFoodStats().getFoodLevel() > 6 || !Foodcheck.getValBoolean()) {
            if (ObstacleCheck.getValBoolean()) {
                if (this.canSprint()) {
                    mc.player.setSprinting(true);
                }
            } else {
                mc.player.setSprinting(true);
            }
            if (mc.player.isSprinting()) {
                if (InstarunTimer.getValBoolean() && quicktimerrun > 0) {
                    startedquickrun = true;
                    setTickLength((float) 33.333);
                    quicktimerrun--;
                    if (quicktimerrun < 1)
                        setTickLength(50);
                }
            } else {
                if (startedquickrun) {
                    startedquickrun = false;
                    setTickLength(50);
                }
                quicktimerrun = Fastspeedup.getValDouble();
            }
            if (Backwards.getValBoolean()) {
                if (!mc.player.isElytraFlying() && Wrapper.mc.gameSettings.keyBindBack.isKeyDown()) {
                    if (mc.player.moveForward > 0.0f && !mc.player.collidedHorizontally) {
                        mc.player.setSprinting(true);
                    }
                    if (mc.player.onGround) {
                        mc.player.motionX *= 1.092;
                        mc.player.motionZ *= 1.092;
                    }
                    final double sqrt = Math.sqrt(Math.pow(mc.player.motionX, 2.0) + Math.pow(mc.player.motionZ, 2.0));
                    final double n = 0.6500000262260437;
                    if (sqrt > n) {
                        mc.player.motionX = mc.player.motionX / sqrt * n;
                        mc.player.motionZ = mc.player.motionZ / sqrt * n;
                    }
                }
            }
        }
        super.onClientTick(event);
    }

    boolean canSprint() {
        if (!mc.player.onGround) {
            return false;
        }
        if (mc.player.isSprinting()) {
            return false;
        }
        if (mc.player.isOnLadder()) {
            return false;
        }
        if (mc.player.isInWater()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (mc.player.collidedHorizontally) {
            return false;
        }
        if (mc.player.moveForward < 0.1F) {
            return false;
        }
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.getFoodStats().getFoodLevel() < 6) {
            return false;
        }
        if (mc.player.isRiding()) {
            return false;
        }
        return !mc.player.isPotionActive(MobEffects.BLINDNESS);
    }
}
