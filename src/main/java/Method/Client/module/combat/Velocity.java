package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.directionSpeed;

public class Velocity extends Module {

    Setting mode = setmgr.add(new Setting("Mode", this, "Simple", "Simple", "AAC", "Fast",
            "YPort", "AAC4Flag", "Pull", "Airmove", "HurtPacket"));
    Setting XMult = setmgr.add(new Setting("XMultipl", this, 0, 0, 10, false, mode, "Simple", 1));
    Setting YMult = setmgr.add(new Setting("YMultipl", this, 0, 0, 10, false, mode, "Simple", 2));
    Setting ZMult = setmgr.add(new Setting("ZMultipl", this, 0, 0, 10, false, mode, "Simple", 3));
    Setting onPacket = setmgr.add(new Setting("Only Packet", this, true, mode, "Simple", 4));
    Setting CancelPacket = setmgr.add(new Setting("CancelPacket", this, true, mode, "Simple", 5));
    Setting Super = setmgr.add(new Setting("Super", this, true, mode, "Pull", 1));
    Setting Pushspeed = setmgr.add(new Setting("Pushspeed", this, .25, 0.0001, .4, false, mode, "Airmove", 2));
    Setting Pushstart = setmgr.add(new Setting("Pushstart", this, 8, 2, 9, false, mode, "Airmove", 3));


    private double motionX;
    private double motionZ;
    private final TimerUtils timer = new TimerUtils();

    public Velocity() {
        super("Velocity", Keyboard.KEY_NONE, Category.COMBAT, "Velocity");
    }

    @Override
    public void onClientTick(ClientTickEvent event) {

        if (mode.getValString().equalsIgnoreCase("AAC")) {
            if (mc.player.hurtTime > 0 && mc.player.hurtTime <= 7) {
                mc.player.motionX *= 0.5;
                mc.player.motionZ *= 0.5;
            }
            if (mc.player.hurtTime > 0 && mc.player.hurtTime < 6) {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Fast")) {
            if (mc.player.hurtTime < 9 && !mc.player.onGround) {
                double yaw = mc.player.rotationYawHead;
                yaw = Math.toRadians(yaw);
                double dX = (-Math.sin(yaw)) * 0.08;
                double dZ = Math.cos(yaw) * 0.08;
                if (mc.player.getHealth() >= 6.0f) {
                    mc.player.motionX = dX;
                    mc.player.motionZ = dZ;
                }
            }
        }

        if (mode.getValString().equalsIgnoreCase("Simple") && !onPacket.getValBoolean()) {
            if (mc.player.hurtTime > 0 && mc.player.fallDistance < 3.0f) {
                if (timer.isDelay(100)) {
                    if (Utils.isMovinginput()) {
                        mc.player.motionX *= XMult.getValDouble();
                        mc.player.motionZ *= ZMult.getValDouble();
                    } else {
                        mc.player.motionX *= XMult.getValDouble() + 0.2;
                        mc.player.motionZ *= ZMult.getValDouble() + 0.2;
                    }
                    mc.player.motionY -= YMult.getValDouble();
                    mc.player.motionY += YMult.getValDouble();
                    timer.setLastMS();
                }
            }
        }

        if (mode.getValString().equalsIgnoreCase("AAC4Flag") && (mc.player.hurtTime == 3 || mc.player.hurtTime == 4)) {
            final double[] directionSpeedVanilla = directionSpeed(.05D);
            mc.player.motionX = directionSpeedVanilla[0];
            mc.player.motionZ = directionSpeedVanilla[1];
        }

        if (mode.getValString().equalsIgnoreCase("Pull")) {
            if (mc.player.hurtTime == 9) {
                this.motionX = mc.player.motionX;
                this.motionZ = mc.player.motionZ;

            }
            if (Super.getValBoolean()) {
                if (mc.player.hurtTime == 8) {
                    mc.player.motionX = -this.motionX * 0.45D;
                    mc.player.motionZ = -this.motionZ * 0.45D;
                }
            } else if (mc.player.hurtTime == 4) {
                mc.player.motionX = -this.motionX * 0.6D;
                mc.player.motionZ = -this.motionZ * 0.6D;
            }
        }
        if (mode.getValString().equalsIgnoreCase("Airmove")) {
            if (mc.player.hurtTime == 9) {
                this.motionX = mc.player.motionX;
                this.motionZ = mc.player.motionZ;
            } else if ((double) mc.player.hurtTime == Pushstart.getValDouble() - 1.0D) {
                mc.player.motionX *= -Pushspeed.getValDouble();
                mc.player.motionZ *= -Pushspeed.getValDouble();
            }
        }
        if (mode.getValString().equalsIgnoreCase("HurtPacket")) {
            if (mc.player.hurtResistantTime > 18)
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 12.0d, mc.player.posZ, false));
        }
        super.onClientTick(event);
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mode.getValString().equalsIgnoreCase("Simple") && onPacket.getValBoolean()) {
            if (CancelPacket.getValBoolean()) {
                if (packet instanceof SPacketEntityVelocity) {
                    final SPacketEntityVelocity packet2 = (SPacketEntityVelocity) packet;
                    return !(packet2.getEntityID() == mc.player.getEntityId());
                }
                if (packet instanceof SPacketExplosion) {
                    if (YMult.getValDouble() == 0 && XMult.getValDouble() == 0 && ZMult.getValDouble() == 0)
                        return false;
                }

                return true;
            }
            if (timer.isDelay(100)) {
                if (packet instanceof SPacketEntityVelocity) {
                    final SPacketEntityVelocity packet2 = (SPacketEntityVelocity) packet;
                    packet2.motionY *= YMult.getValDouble();
                    packet2.motionX *= XMult.getValDouble();
                    packet2.motionZ *= ZMult.getValDouble();
                }
                if (packet instanceof SPacketExplosion) {
                    final SPacketExplosion packet2 = (SPacketExplosion) packet;
                    packet2.motionY *= YMult.getValDouble();
                    packet2.motionX *= XMult.getValDouble();
                    packet2.motionZ *= ZMult.getValDouble();
                }
                timer.setLastMS();
            }
        }

        if (mode.getValString().equalsIgnoreCase("YPort")) {
            if (mc.player.hurtTime >= 8) {
                mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY + 2D, mc.player.lastTickPosZ);
                mc.player.motionY -= .3;
                mc.player.motionX *= .8;
                mc.player.motionZ *= .8;
            }
        }
        return true;
    }


}


