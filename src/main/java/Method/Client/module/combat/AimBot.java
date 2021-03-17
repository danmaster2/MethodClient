package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class AimBot extends Module {

    Setting priority = setmgr.add(new Setting("priority", this, "Closest", "Closest", "Health"));
    Setting walls = setmgr.add(new Setting("walls", this, false));
    Setting yaw = setmgr.add(new Setting("yaw", this, 15, 0, 50, false));
    Setting pitch = setmgr.add(new Setting("pitch", this, 15, 0, 50, false));
    Setting range = setmgr.add(new Setting("range", this, 4.7, .1, 10, false));
    Setting FOV = setmgr.add(new Setting("FOV", this, 90, 1, 180, false));
    Setting Silent = setmgr.add(new Setting("Silent", this, false));
    Setting Mobs = setmgr.add(new Setting("Mobs", this, true));
    Setting Animals = setmgr.add(new Setting("Animals", this, false));

    public EntityLivingBase target;

    public AimBot() {
        super("AimBot", Keyboard.KEY_NONE, Category.COMBAT, "Aims to enemys");
    }

    @Override
    public void onDisable() {
        this.target = null;
        super.onDisable();
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {

        if (side == Connection.Side.OUT)
            if (Silent.getValBoolean()) {
                if ((packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation)) {
                    updateTarget();
                    final CPacketPlayer packet2 = (CPacketPlayer) packet;
                    float[] rot = Utils.getNeededRotations(this.target.getPositionVector().add(0, this.target.getEyeHeight() / 2, 0), (float) yaw.getValDouble(), (float) pitch.getValDouble());
                    packet2.yaw = rot[0];
                    packet2.pitch = rot[1];
                }
                this.target = null;
            }
        return true;
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        updateTarget();
        if (!Silent.getValBoolean()) {
            float[] rot = Utils.getNeededRotations(this.target.getPositionVector().add(0, this.target.getEyeHeight() / 2, 0), (float) yaw.getValDouble(), (float) pitch.getValDouble());
            mc.player.rotationYaw = rot[0];
            mc.player.rotationPitch = rot[1];
        }
        this.target = null;
        super.onClientTick(event);
    }

    void updateTarget() {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) object;
                if (check(entity)) {
                    this.target = entity;
                }
            }
        }
    }


    public boolean check(EntityLivingBase entity) {
        if (Checks(entity, FOV)) return false;
        if (!ValidUtils.isInAttackRange(entity, (float) range.getValDouble())) {
            return false;
        }
        if (!ValidUtils.pingCheck(entity)) {
            return false;
        }
        if (!isPriority(entity)) {
            return false;
        }
        if (!walls.getValBoolean()) {
            return mc.player.canEntityBeSeen(entity);
        }
        return true;
    }

    boolean Checks(EntityLivingBase entity, Setting fov) {
        if (entity instanceof EntityArmorStand) {
            return true;
        }
        if (!ValidUtils.isNoScreen()) {
            return true;
        }
        if (entity == mc.player) {
            return true;
        }
        if (entity.isDead) {
            return true;
        }
        if (ValidUtils.isBot(entity)) {
            return true;
        }
        if ((ValidUtils.isFriendEnemy(entity))) {
            return true;
        }
        if (!ValidUtils.isInAttackFOV(entity, (int) fov.getValDouble())) {
            return true;
        }
        if (Animals.getValBoolean())
            if (entity instanceof IAnimals)
                return false;

        if (Mobs.getValBoolean())
            if (entity instanceof IMob)
                return false;
        if (entity instanceof EntityPlayer)
            return false;
        return true;
    }


    boolean isPriority(EntityLivingBase entity) {
        return priority.getValString().equalsIgnoreCase("Closest") && ValidUtils.isClosest(entity, target) || priority.getValString().equalsIgnoreCase("Health") && ValidUtils.isLowHealth(entity, target);
    }


}
