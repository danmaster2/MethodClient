package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.Utils;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.isMoving;

public class Strafe extends Module {

    public Strafe() {
        super("Strafe", Keyboard.KEY_NONE, Category.COMBAT, "Strafe");
    }

    Setting mode = setmgr.add(new Setting("Strafe Mode", this, "Vanilla", "Vanilla", "Fast", "Bypass"));
    Setting jump = setmgr.add(new Setting("FastJump", this, true, mode, "Bypass", 1));
    Setting extraYBoost = setmgr.add(new Setting("extraYBoost", this, 0, 0, 1, false, mode, "Fast", 1));
    Setting jumpDetect = setmgr.add(new Setting("jumpDetect", this, false, mode, "Fast", 1));
    Setting speedDetect = setmgr.add(new Setting("speedDetect", this, false, mode, "Fast", 1));
    Setting multiplier = setmgr.add(new Setting("multiplier", this, 1, 0, 1, false, mode, "Fast", 1));
    Setting ground = setmgr.add(new Setting("ground", this, true, mode, "Vanilla", 1));

    int waitCounter = 0;
    int forward = 1;
    private double motionSpeed;
    private int currentState;
    private double prevDist;

    @Override
    public void onDisable() {
        mc.timer.tickLength = (float) (50);
        super.onEnable();
    }

    private double getBaseMotionSpeed() {
        double v = 0.272;
        if (mc.player.isPotionActive(MobEffects.SPEED) && speedDetect.getValBoolean()) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            v *= 1.0 + 0.2 * amplifier;
        }
        return v;
    }

    private static int calcl(float var0) {
        float var2;
        return (var2 = var0 - (float) 0.0) == 0.0F ? 0 : (var2 < 0.0F ? -1 : 1);
    }


    private static int Call(double var0) {
        double var4;
        return (var4 = var0 - 0.0) == 0.0D ? 0 : (var4 < 0.0D ? -1 : 1);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mode.getValString().equalsIgnoreCase("Fast")) {
            double v;
            double v1;
            double v2;
            double v3;

            switch (this.currentState) {
                case 0: {
                    this.currentState += 1;
                    this.prevDist = 0.0;
                    break;
                }
                case 2: {
                    v = 0.40123128 + extraYBoost.getValDouble();
                    if ((!(calcl(mc.player.moveForward) == 0) || (calcl(mc.player.moveStrafing)) != 0) && (mc.player.onGround ? 1 : 0) != 0) {
                        if ((mc.player.isPotionActive(MobEffects.JUMP_BOOST) ? 1 : 0) != 0 && (jumpDetect.getValBoolean() ? 1 : 0) != 0) {
                            v += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
                        }
                        mc.player.motionY = (v);
                        this.motionSpeed *= 2.149;
                    }
                    break;
                }
                case 3: {
                    this.motionSpeed = this.prevDist - 0.76 * (this.prevDist - this.getBaseMotionSpeed());
                    break;
                }
                default: {
                    if ((!(mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() <= 0) || (mc.player.collidedVertically ? 1 : 0) != 0) && (this.currentState) > 0) {
                        if ((calcl(mc.player.moveForward)) == 0 && (calcl(mc.player.moveStrafing)) == 0) {
                            currentState = 0;
                        } else {
                            currentState = 1;
                        }
                    }
                    this.motionSpeed = this.prevDist - this.prevDist / 159.0;
                    break;
                }
            }
            this.motionSpeed = Math.max(this.motionSpeed, this.getBaseMotionSpeed());
            v1 = mc.player.movementInput.moveForward;
            v2 = mc.player.movementInput.moveStrafe;
            v3 = mc.player.rotationYaw;
            if ((Call(v1)) == 0 && (Call(v2)) == 0) {
                mc.player.motionX = (0.0);
                mc.player.motionZ = (0.0);
            }
            if ((Call(v1)) != 0 && (Call(v2)) != 0) {
                v1 *= Math.sin(0.7853981633974483);
                v2 *= Math.cos(0.7853981633974483);
            }
            mc.player.motionX = ((v1 * this.motionSpeed * -Math.sin(Math.toRadians(v3)) + v2 * this.motionSpeed * Math.cos(Math.toRadians(v3))) * (multiplier.getValDouble() * 0.99));
            mc.player.motionZ = ((v1 * this.motionSpeed * Math.cos(Math.toRadians(v3)) - v2 * this.motionSpeed * -Math.sin(Math.toRadians(v3))) * (multiplier.getValDouble() * 0.99));
            this.currentState += 1;

        }
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Fast")) {
            this.prevDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
        }

        if (isMoving(mc.player) && mode.getValString().equalsIgnoreCase("Vanilla")) {

            if (mc.player.isSneaking() || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.isInLava() || mc.player.isInWater() || mc.player.capabilities.isFlying)
                return;
            if (!ground.getValBoolean()) {
                if (mc.player.onGround)
                    return;
            }
            float playerSpeed = 0.2873f;
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
                playerSpeed *= (1.0f + 0.2f * (amplifier + 1));
            }
            if (mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) {
                mc.player.motionX = (0.0d);
                mc.player.motionZ = (0.0d);
            } else Utils.directionSpeed(playerSpeed);
        }

        if (mode.getValString().equalsIgnoreCase("Bypass")) {
            mc.timer.tickLength = 45.5F;
            boolean boost = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90.0F;
            if ((this.isPlayerTryingMoveForward() || this.isPlayerTryingStrafe()) && mc.player.onGround) {
                mc.player.motionY = 0.4D;
            }

            if (mc.player.moveForward != 0.0F) {
                if (!mc.player.isSprinting()) {
                    mc.player.setSprinting(true);
                }

                float yaw = mc.player.rotationYaw;
                if (mc.player.moveForward > 0.0F) {
                    if (mc.player.movementInput.moveStrafe != 0.0F) {
                        yaw += mc.player.movementInput.moveStrafe > 0.0F ? -45.0F : 45.0F;
                    }

                    this.forward = 1;
                    mc.player.moveForward = 1.5F;
                    mc.player.moveStrafing = 1.5F;
                } else if (mc.player.moveForward < 0.0F) {
                    if (mc.player.movementInput.moveStrafe != 0.0F) {
                        yaw += mc.player.movementInput.moveStrafe > 0.0F ? 45.0F : -45.0F;
                    }

                    this.forward = -1;
                    mc.player.moveForward = -1.5F;
                    mc.player.moveStrafing = 1.5F;
                }

                if (mc.player.onGround) {
                    float f = (float) Math.toRadians(yaw);
                    if (jump.getValBoolean() && mc.gameSettings.keyBindJump.isPressed()) {
                        Move(f);
                    }
                } else {
                    if (this.waitCounter < 1) {
                        ++this.waitCounter;
                        return;
                    }

                    this.waitCounter = 0;
                    double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                    double speed = boost ? 1.05D : 1.025D;
                    if (mc.player.motionY < 0.0D) {
                        speed = 1.0D;
                    }

                    double direction = Math.toRadians(yaw);
                    mc.player.motionX = -Math.sin(direction) * speed * currentSpeed * (double) this.forward;
                    mc.player.motionZ = Math.cos(direction) * speed * currentSpeed * (double) this.forward;
                }
            }

            if (!this.isPlayerTryingMoveForward() && !this.isPlayerTryingStrafe()) {
                mc.player.motionX = 0.0D;
                mc.player.motionZ = 0.0D;
            }

        }
        super.onClientTick(event);
    }

    private void Move(float f) {
        if (jump.getValBoolean())
            mc.player.motionY = 0.4D;
        mc.player.motionX -= (double) (MathHelper.sin(f) * 0.2F) * (double) this.forward;
        mc.player.motionZ += (double) (MathHelper.cos(f) * 0.2F) * (double) this.forward;
    }


    public boolean isPlayerTryingMoveForward() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown();
    }

    public boolean isPlayerTryingStrafe() {
        return mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown();
    }

}
