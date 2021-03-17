package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;

public class Criticals extends Module {
    TimerUtils timer = new TimerUtils();
    boolean cancelSomePackets;
    Setting mode = setmgr.add(new Setting("Mode", this, "Packet", "Packet", "Simple", "Groundspoof", "Jump",
            "Fpacket", "Bpacket", "Falldist", "MiniJump", "NBypass"));
    Setting ShowCrit = setmgr.add(new Setting("ShowCrit", this, true));


    public Criticals() {
        super("Auto Criticals", Keyboard.KEY_NONE, Category.COMBAT, "Criticals on hit");
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mc.player.onGround) {
            if (side == Connection.Side.OUT) {
                if (packet instanceof CPacketUseEntity) {
                    CPacketUseEntity attack = (CPacketUseEntity) packet;
                    if (attack.getAction() == Action.ATTACK) {
                        if (mode.getValString().equalsIgnoreCase("Bpacket")) {
                            try {
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11D, mc.player.posZ, true));
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579D, mc.player.posZ, false));
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.3579E-6D, mc.player.posZ, false));
                            } catch (Exception ignored) {
                            }
                        }
                        if (mode.getValString().equalsIgnoreCase("NBypass")) {
                            mc.player.motionY = 0.41999998688697815D;
                            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                                mc.player.motionY += (float) (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1F;
                            }
                            if (mc.player.isSprinting()) {
                                float var1 = mc.player.rotationYaw * 0.017453292F;
                                mc.player.motionX -= MathHelper.sin(var1) * 0.2F;
                                mc.player.motionZ += MathHelper.cos(var1) * 0.2F;
                            }
                            mc.player.isAirBorne = true;
                        }
                        if (mode.getValString().equalsIgnoreCase("Simple")) {
                            if (canJump()) {
                                mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY - 0.5D, mc.player.posZ);
                                mc.player.setSprinting(true);
                            } else {
                                mc.player.motionY = -0.1D;
                            }
                        }
                        if (mode.getValString().equalsIgnoreCase("MiniJump")) {
                            mc.player.jump();
                            mc.player.motionY -= 0.30000001192092896;
                        }
                        if (mode.getValString().equalsIgnoreCase("Fpacket")) {
                            doCritical();
                        }
                        if (mode.getValString().equalsIgnoreCase("Falldist")) {
                            mc.player.motionY -= -.001;
                            mc.player.fallDistance = 9999;
                            mc.player.fall(20, 0);
                        }
                        if (mode.getValString().equalsIgnoreCase("Groundspoof")) {
                            mc.player.onGround = false;
                            mc.player.isAirBorne = true;
                        }
                        if (mode.getValString().equalsIgnoreCase("Packet")) {
                            if (mc.player.collidedVertically && this.timer.isDelay(500)) {
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0627, mc.player.posZ, false));
                                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                this.timer.setLastMS();
                                this.cancelSomePackets = true;
                            }
                        } else if (mode.getValString().equalsIgnoreCase("Jump")) {
                            if (canJump()) {
                                mc.player.jump();
                            }

                        }
                        if (ShowCrit.getValBoolean()) {
                            Entity entity = attack.getEntityFromWorld(mc.world);
                            if (entity != null) {
                                mc.player.onCriticalHit(entity);
                            }
                        }
                    }
                }
            } else if (mode.getValString().equalsIgnoreCase("Packet") && packet instanceof CPacketPlayer) {
                if (cancelSomePackets) {
                    cancelSomePackets = false;
                    return false;
                }
            }
        }
        return true;
    }


    private void doCritical() {
        if (mc.player.isInLava() || mc.player.isInWater()) return;
        double posX = mc.player.posX;
        double posY = mc.player.posY;
        double posZ = mc.player.posZ;
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(posX, posY + 0.0625D, posZ, false));
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, false));
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(posX, posY + 1.1E-5D, posZ, false));
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(posX, posY, posZ, false));
    }

    boolean canJump() {
        if (mc.player.isOnLadder()) {
            return false;
        }
        if (mc.player.isInWater()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isRiding()) {
            return false;
        }
        return !mc.player.isPotionActive(MobEffects.BLINDNESS);
    }
}
