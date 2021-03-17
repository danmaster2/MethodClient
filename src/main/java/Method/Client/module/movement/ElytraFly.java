
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.directionSpeed;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ElytraFly extends Module {

    /////////////////////

    Setting mode = setmgr.add(new Setting("mode", this, "Control", "BYPASS", "Control", "Boost",
            "Try1", "Legit+", "Mouse", "Rocket"));
    Setting speed = setmgr.add(new Setting("speed", this, 1, 0, 5, false));
    Setting autoStart = setmgr.add(new Setting("autoStart", this, false));
    Setting disableInLiquid = setmgr.add(new Setting("disableInLiquid", this, false));
    Setting infiniteDurability = setmgr.add(new Setting("Packet Durability", this, false));
    Setting Flatfly = setmgr.add(new Setting("Flatfly", this, false, mode, "Control", 6));
    Setting BuildupTicks = setmgr.add(new Setting("BuildupTicks", this, 0, 0, 200, false, mode, "Control", 7));
    Setting noKick = setmgr.add(new Setting("noKick", this, false, mode, "Control", 8));
    Setting RandomFlap = setmgr.add(new Setting("RandomFlap", this, false));
    Setting Yboost = setmgr.add(new Setting("Yboost", this, false, mode, "Boost", 8));
    Setting Flatboost = setmgr.add(new Setting("Flatboost", this, false, mode, "Boost", 8));
    Setting Fallspeedboost = setmgr.add(new Setting("FlatFall% ", this, 99.95, 10, 99.95, false, mode, "Boost", 7));
    Setting Fallspeed = setmgr.add(new Setting("Fall multiplier", this, 99.95, 10, 99.95, false, mode, "Control", 7));
    Setting StopPitch = setmgr.add(new Setting("StopPitch", this, 0, 0, 90, false, mode, "Control", 7));
    Setting Speedme = setmgr.add(new Setting("Speed Weird", this, 1, 0.01D, 3, false, mode, "Try1", 6));
    Setting upspeed = setmgr.add(new Setting("upspeed", this, 1, 0, 3, false, mode, "Mouse", 6));
    Setting SpeedMulti = setmgr.add(new Setting("Burner Speed", this, 2, 0, 10, false, mode, "Rocket", 7));
    Setting AutoRocket = setmgr.add(new Setting("AutoRocket", this, false, mode, "Rocket", 6));
    Setting RocketTicks = setmgr.add(new Setting("RocketTicks ", this, 22, 0, 100, false, mode, "Rocket", 7));
    Setting Lockmove = setmgr.add(new Setting("Lockmove", this, false, mode, "Rocket", 6));
    Setting AfterBurner = setmgr.add(new Setting("After Burner", this, false, mode, "Rocket", 6));
    Setting TicksAfter = setmgr.add(new Setting("Burner Ticks", this, 22, 0, 60, false, mode, "Rocket", 7));

    double yposperson;
    double Lazyticks = 0;
    EntityFireworkRocket wasBoosted = null;

    private final TimerUtils timer = new TimerUtils();
    private final TimerUtils Fireworktimer1 = new TimerUtils();
    private final TimerUtils Fireworktimer2 = new TimerUtils();

    public ElytraFly() {
        super("ElytraFly", Keyboard.KEY_NONE, Category.MOVEMENT, "Elytra Fly");
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // ensure player has an elytra on before running any code
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA)
            return;

        // liquid check
        if (this.disableInLiquid.getValBoolean() && (mc.player.isInWater() || mc.player.isInLava())) {
            if (mc.player.isElytraFlying()) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
            return;
        }

        // automatic jump start
        if (this.autoStart.getValBoolean()) {
            if (!mc.player.isElytraFlying()) {
                if (mc.player.posY + .02 < mc.player.lastTickPosY && !mc.player.onGround) {
                    mc.player.posY = yposperson;
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                } else {
                    yposperson = mc.player.posY;
                }
            }
        }
        // ensure the player is in the elytra flying state
        if (mc.player.isElytraFlying()) {
            if (RandomFlap.getValBoolean()) {
                mc.player.rotateElytraX = (float) Math.random();
                mc.player.rotateElytraY = (float) Math.random();
                mc.player.rotateElytraZ = (float) Math.random();
            }

            if (this.mode.getValString().equalsIgnoreCase("Legit+")) {
                mc.player.jumpMovementFactor *= 1.01222f;
                mc.player.fallDistance = 0;
                mc.player.moveStrafing += 0.1F;
                mc.player.velocityChanged = true;
                if (mc.player.cameraPitch > 88) {
                    mc.player.addVelocity(.1, 0, .1);
                }
                final double[] directionSpeedVanilla = directionSpeed(.02);
                if (mc.player.cameraPitch < 0) {
                    mc.player.addVelocity(directionSpeedVanilla[0], -.001, directionSpeedVanilla[1]);
                }
            }
            if (this.mode.getValString().equalsIgnoreCase("Rocket")) {
                mc.player.attackEntityFrom(DamageSource.FIREWORKS, 10);
                if (wasBoosted != null) {
                    float speedScaled = (float) (speed.getValDouble() * 0.05f) * 10;
                    if (Fireworktimer1.isDelay((long) (TicksAfter.getValDouble() * 100)) && AfterBurner.getValBoolean()) {
                        wasBoosted = null;
                        speedScaled *= SpeedMulti.getValDouble();
                        Fireworktimer1.setLastMS();
                    } else if (Lockmove.getValBoolean()) {
                        this.freezePlayer(mc.player);
                    }
                    final double[] directionSpeedVanilla = directionSpeed(speedScaled);
                    if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                        mc.player.motionX += directionSpeedVanilla[0];
                        mc.player.motionZ += directionSpeedVanilla[1];
                    }
                } else {
                    if (AutoRocket.getValBoolean() && Fireworktimer2.isDelay((long) RocketTicks.getValDouble() * 100)) {
                        if (mc.player.getHeldItemMainhand().item == Items.FIREWORKS || mc.player.getHeldItemOffhand().item == Items.FIREWORKS) {
                            mc.rightClickMouse();
                        }
                        Fireworktimer2.setLastMS();
                    }
                    if (AfterBurner.getValBoolean())
                        for (Entity entity : mc.world.loadedEntityList) {
                            if (entity instanceof EntityFireworkRocket) {
                                EntityFireworkRocket e = (EntityFireworkRocket) entity;
                                if (e.boostedEntity == mc.player && !e.isGlowing()) {
                                    e.setGlowing(true);
                                    wasBoosted = e;
                                    Fireworktimer1.setLastMS();
                                    break;
                                }
                            }
                        }
                }
            }
            if (this.mode.getValString().equalsIgnoreCase("Boost")) {
                float pitch = mc.player.rotationPitch;
                final float speedScaled = (float) (speed.getValDouble() * 0.05f);
                final double[] directionSpeedVanilla = directionSpeed(speedScaled);
                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX += directionSpeedVanilla[0];
                    mc.player.motionZ += directionSpeedVanilla[1];
                }
                mc.player.motionY += (Yboost.getValBoolean() ? sin(Math.toRadians(pitch)) * 0.05 : 0);

                if (Flatboost.getValBoolean())
                    mc.player.motionY = (.03 * cos(((Math.PI * Math.abs(mc.player.rotationPitch)) / 90) + Math.PI)) + (.05 * (Fallspeedboost.getValDouble() / 100));
                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.player.motionY += 0.05;
                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.player.motionY -= 0.05;
            }
            if (mode.getValString().equalsIgnoreCase("Try1")) {
                if (mc.player.motionY < 0 && mc.player.isAirBorne || mc.player.onGround) {
                    mc.player.motionY = -0.125f;
                    mc.player.jumpMovementFactor *= 1.01227f + Speedme.getValDouble();
                    mc.player.noClip = true;
                    mc.player.fallDistance = 0;
                    mc.player.onGround = true;
                    mc.player.moveStrafing += 0.8F + Speedme.getValDouble();
                    mc.player.jumpMovementFactor += 0.2F + Speedme.getValDouble();
                    mc.player.velocityChanged = true;
                    if (mc.player.ticksExisted % 8 == 0 && mc.player.posY <= 240) {
                        mc.player.motionY = 0.02f;
                    }
                }
            }


            if (this.mode.getValString().equalsIgnoreCase("Control")) {
                this.runNoKick();
                final double[] directionSpeedPacket = directionSpeed(this.speed.getValDouble() / Math.max(Lazyticks, 1));
                if (mc.player.movementInput.moveStrafe == 0 && mc.player.movementInput.moveForward == 0) {
                    Lazyticks = 0;
                    this.freezePlayer(mc.player);
                    mc.player.motionY = (.03 * cos(((Math.PI * Math.abs(mc.player.rotationPitch)) / 90) + Math.PI)) + (.05 * (Fallspeed.getValDouble() / 100));
                    if (mc.player.movementInput.sneak) {
                        mc.player.motionY = -this.speed.getValDouble();
                    }
                } else if (mc.player.rotationPitch > StopPitch.getValDouble() - .01) {
                    if (BuildupTicks.getValDouble() > 0) {
                        if (Lazyticks == 0) {
                            Lazyticks = BuildupTicks.getValDouble();
                        }
                        if (Lazyticks > 1)
                            Lazyticks /= 1.03;
                    }
                    this.freezePlayer(mc.player);

                    // mc.player.rotationPitch // at zero this is 0! || up is neg || down is pos
                    mc.player.motionY = (.03 * cos(((Math.PI * Math.abs(mc.player.rotationPitch)) / 90) + Math.PI)) + (.05 * (Fallspeed.getValDouble() / 100));

                    if (mc.player.movementInput.sneak) {
                        mc.player.motionY = -this.speed.getValDouble();
                    }

                    if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                        mc.player.motionX = directionSpeedPacket[0];
                        mc.player.motionZ = directionSpeedPacket[1];
                    }
                } else {
                    if (mc.player.posY < mc.player.lastTickPosY && timer.isDelay(100)) {
                        mc.player.rotationPitch = 0;
                        this.timer.setLastMS();
                    }
                }
            }

            if (this.mode.getValString().equalsIgnoreCase("Mouse")) {
                double[] dir = Utils.directionSpeed(speed.getValDouble());
                if (Module.mc.player.movementInput.moveStrafe == 0.0F && Module.mc.player.movementInput.moveForward == 0.0F) {
                    Module.mc.player.motionX = 0.0D;
                    Module.mc.player.motionZ = 0.0D;
                } else {
                    Module.mc.player.motionX = dir[0];
                    Module.mc.player.motionZ = dir[1];
                    mc.player.motionX -= Module.mc.player.motionX * (double) (Math.abs(Module.mc.player.rotationPitch) + 90.0F) / 90.0D - Module.mc.player.motionX;
                    mc.player.motionZ -= Module.mc.player.motionZ * (double) (Math.abs(Module.mc.player.rotationPitch) + 90.0F) / 90.0D - Module.mc.player.motionZ;
                }

                float y = 0.0F;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    y = (float) upspeed.getValDouble();
                }

                double Ymove = (double) y + -this.degToRad(Module.mc.player.rotationPitch) * (double) Module.mc.player.movementInput.moveForward;
                if (upspeed.getValDouble() == 0.0F && Ymove > 0.0D) {
                    Ymove = 0.0D;
                }

                Module.mc.player.motionY = Ymove;
            }
        }
        if (this.mode.getValString().equalsIgnoreCase("BYPASS")) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY = 0.02f;
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY = -0.2f;
            }

            if (mc.player.ticksExisted % 8 == 0 && mc.player.posY <= 240) {
                mc.player.motionY = 0.02f;
            }

            mc.player.capabilities.isFlying = true;
            mc.player.capabilities.setFlySpeed(0.025f);

            final double[] directionSpeedBypass = directionSpeed(0.52f);

            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionX = directionSpeedBypass[0];
                mc.player.motionZ = directionSpeedBypass[1];
            } else {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
        }


        if (this.infiniteDurability.getValBoolean()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }


    }

    public double degToRad(double deg) {
        return deg * 0.01745329238474369D;
    }

    private void runNoKick() {
        if (noKick.getValBoolean() && !mc.player.isElytraFlying()) {
            if (mc.player.ticksExisted % 4 == 0) {
                mc.player.motionY = -0.04f;
            }
        }
    }

    private void freezePlayer(EntityPlayer player) {
        player.motionX = 0;
        player.motionY = 0;
        player.motionZ = 0;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof CPacketPlayer.PositionRotation) {
            if (mc.player.rotationPitch > -StopPitch.getValDouble() && mode.getValString().equalsIgnoreCase("Control")) {
                if (mc.player.isElytraFlying() && Flatfly.getValBoolean()) {
                    CPacketPlayer.PositionRotation rotation = (CPacketPlayer.PositionRotation) packet;
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(rotation.x, rotation.y, rotation.z, rotation.onGround));
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void onDisable() {
        if (mc.player != null)
            mc.player.capabilities.isFlying = false;
    }

}
