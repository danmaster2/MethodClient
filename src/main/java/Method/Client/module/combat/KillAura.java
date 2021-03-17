package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", Keyboard.KEY_NONE, Category.COMBAT, "KillAura");
    }

    public Setting priority = setmgr.add(new Setting("priority Mode", this, "Closest", "Closest", "Health"));
    public Setting walls = setmgr.add(new Setting("walls", this, true));
    public Setting autoDelay = setmgr.add(new Setting("autoDelay", this, false));
    public Setting packetReach = setmgr.add(new Setting("packetReach", this, false));
    public Setting minCPS = setmgr.add(new Setting("minCPS", this, 5, 0, 30.0, false));
    public Setting maxCPS = setmgr.add(new Setting("maxCPS", this, 8, 0, 30, false));
    public Setting packetRange = setmgr.add(new Setting("packetRange", this, 5, 0, 100.0, false));
    public Setting range = setmgr.add(new Setting("range", this, 5, 0, 10.0, false));
    public Setting FOV = setmgr.add(new Setting("FOV", this, 180, 0, 180, false));
    public Setting Mobs = setmgr.add(new Setting("Mobs", this, true));
    public Setting Animals = setmgr.add(new Setting("Animals", this, false));
    public Setting SpoofAngle = setmgr.add(new Setting("SpoofAngle", this, true));

    public TimerUtils timer = new TimerUtils();
    public EntityLivingBase target;

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        killAuraUpdate();
        killAuraAttack(target);
        super.onClientTick(event);
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        killAuraUpdate();
        if (side == Connection.Side.OUT)
            if (SpoofAngle.getValBoolean()) {
                if ((packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation)) {
                    final CPacketPlayer packet2 = (CPacketPlayer) packet;
                    float[] rot = Utils.getNeededRotations(this.target.getPositionVector().add(0, this.target.getEyeHeight() / 2, 0), 0, 0);
                    packet2.yaw = rot[0];
                    packet2.pitch = rot[1];
                }
                target = null;
            }
        return true;
    }


    void killAuraUpdate() {
        for (Object object : mc.world.getLoadedEntityList()) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) object;
                if (check(entity)) {
                    target = entity;
                }
            }
        }
    }

    public void killAuraAttack(EntityLivingBase entity) {
        if (entity == null) {
            return;
        }
        if (this.autoDelay.getValBoolean()) {
            if (mc.player.getCooledAttackStrength(0) == 1) {
                processAttack(entity);
                target = null;
            }
        } else {
            int CPS = Utils.random((int) minCPS.getValDouble(), (int) maxCPS.getValDouble());
            int r1 = Utils.random(1, 50), r2 = Utils.random(1, 60), r3 = Utils.random(1, 70);
            if (timer.isDelay((1000 + ((r1 - r2) + r3)) / CPS)) {
                processAttack(entity);
                timer.setLastMS();
                target = null;
            }
        }
    }

    public void processAttack(EntityLivingBase entity) {
        if (isInAttackRange(entity) || !ValidUtils.isInAttackFOV(entity, (int) FOV.getValDouble())) return;

        float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.player.getHeldItemMainhand(), entity.getCreatureAttribute());
        if (this.packetReach.getValBoolean()) {
            double posX = entity.posX - 3.5 * Math.cos(Math.toRadians(Utils.getYaw(entity) + 90.0f));
            double posZ = entity.posZ - 3.5 * Math.sin(Math.toRadians(Utils.getYaw(entity) + 90.0f));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(posX, entity.posY, posZ, Utils.getYaw(entity), Utils.getPitch(entity), mc.player.onGround));
            Wrapper.INSTANCE.sendPacket(new CPacketUseEntity(entity));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
        } else {
            if (autoDelay.getValBoolean())
                Wrapper.INSTANCE.attack(entity);
            else
                Wrapper.INSTANCE.sendPacket(new CPacketUseEntity(entity));
        }
        Wrapper.INSTANCE.swingArm();
        if (sharpLevel > 0.0f)
            mc.player.onEnchantmentCritical(entity);
    }


    boolean isPriority(EntityLivingBase entity) {
        return priority.getValString().equalsIgnoreCase("Closest") && ValidUtils.isClosest(entity, target) || priority.getValString().equalsIgnoreCase("Health") && ValidUtils.isLowHealth(entity, target);
    }

    boolean isInAttackRange(EntityLivingBase entity) {
        return packetReach.getValBoolean() ? !(entity.getDistance(mc.player) <= packetRange.getValDouble()) : !(entity.getDistance(mc.player) <= range.getValDouble());
    }

    public boolean check(EntityLivingBase entity) {
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (!ValidUtils.isNoScreen()) {
            return false;
        }

        if (entity == mc.player) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (entity.deathTime > 0) {
            return false;
        }
        if (ValidUtils.isBot(entity)) {
            return false;
        }
        if ((ValidUtils.isFriendEnemy(entity))) {
            return false;
        }
        if (!ValidUtils.isInAttackFOV(entity, (int) FOV.getValDouble())) {
            return false;
        }
        if (isInAttackRange(entity)) {
            return false;
        }

        if (!ValidUtils.pingCheck(entity)) {
            return false;
        }

        if (!this.walls.getValBoolean()) {
            if (!mc.player.canEntityBeSeen(entity)) {
                return false;
            }
        }
        if (Animals.getValBoolean())
            if (entity instanceof IAnimals)
                return isPriority(entity);

        if (Mobs.getValBoolean())
            if (entity instanceof IMob)
                return isPriority(entity);
        if (entity instanceof EntityPlayer) {
            return isPriority(entity);
        }
        return false;
    }

    public static boolean isInvisible(EntityLivingBase entity) {
        return !entity.isInvisible();
    }

}
