
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Bunnyhop extends Module {
    /////////////////////
    private int airMoves;
    Setting mode = setmgr.add( new Setting("Mode", this, "AAC", "AAC", "NCP", "Timer", "Spartan"));

    public Bunnyhop() {
        super("Bunnyhop", Keyboard.KEY_NONE, Category.MOVEMENT, "Bunny hop");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (mode.getValString().equalsIgnoreCase("Timer")) {
            if (mc.player.onGround) {
                if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
                    mc.player.jump();
                }
                mc.player.motionZ /= 2f;
                mc.player.motionX /= 2f;
                mc.player.motionY += 0.05f;
            } else {
                mc.player.motionY -= 0.03f;
                mc.player.moveStrafing *= 0.07f;
                mc.player.jumpMovementFactor = 0.05f;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Spartan")) {
            if (mc.gameSettings.keyBindForward.isPressed() && !mc.gameSettings.keyBindJump.isPressed()) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    airMoves = 0;
                } else {
                    if (airMoves >= 3)
                        mc.player.jumpMovementFactor = 0.0275F;

                    if (airMoves >= 4 && airMoves % 2 == 0.0) {
                        mc.player.motionY = -0.32F - 0.009 * Math.random();
                        mc.player.jumpMovementFactor = 0.0238F;
                    }

                    airMoves++;
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("AAC")) {
            if (mc.player.isInWater()) {
                return;
            }
            if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    mc.player.motionX *= 1.012D;
                    mc.player.motionZ *= 1.012D;
                } else if (mc.player.motionY > -0.2D) {
                    mc.player.jumpMovementFactor = 0.08F;
                    mc.player.motionY += 0.0003099999999999999999999999999D;
                    mc.player.jumpMovementFactor = 0.07F;
                }
            } else {
                mc.player.motionX = 0D;
                mc.player.motionZ = 0D;
            }
        }

        if (mode.getValString().equalsIgnoreCase("NCP")) {
            if (mc.player != null && mc.world != null) {
                if (mc.gameSettings.keyBindForward.pressed && !mc.player.collidedHorizontally) {
                    mc.gameSettings.keyBindJump.pressed = false;
                    if (mc.player.onGround) {
                        mc.player.jump();
                        mc.player.motionX *= 1.0708F;
                        mc.player.motionZ *= 1.0708F;
                        mc.player.moveStrafing *= 2;
                    } else {
                        mc.player.jumpMovementFactor = 0.0265F;
                    }
                }
            }
        }

    }


    @Override
    public void onDisable() {
        mc.player.jumpMovementFactor = 0.03f;
        super.onDisable();
    }

}
