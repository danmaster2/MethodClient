package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Trigger extends Module {

    public Setting autoDelay = setmgr.add(new Setting("AutoDelay", this, true));
    public Setting advanced = setmgr.add(new Setting("Advanced", this, false));
    public Setting minCPS = setmgr.add(new Setting("MinCPS", this, 4, 1, 20, true));
    public Setting maxCPS = setmgr.add(new Setting("MaxCPS", this, 8, 1, 20, false));

    Setting Mode = setmgr.add(new Setting("Mode", this, "Click", "Click", "Attack"));

    public EntityLivingBase target;

    public TimerUtils timer = new TimerUtils();


    public Trigger() {
        super("Trigger", Keyboard.KEY_NONE, Category.COMBAT, "Triggers");
    }

    @Override
    public void onDisable() {
        this.target = null;
        super.onDisable();
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        updateTarget();
        attackTarget(target);
        super.onClientTick(event);
    }

    void attackTarget(EntityLivingBase target) {
        if (check(target)) {
            if (this.autoDelay.getValBoolean()) {
                if (mc.player.getCooledAttackStrength(0) == 1)
                    processAttack(target, false);
            } else {
                int currentCPS = Utils.random((int) minCPS.getValDouble(), (int) maxCPS.getValDouble());
                if (timer.isDelay(1000 / currentCPS)) {
                    processAttack(target, true);
                    timer.setLastMS();
                }
            }
        }
    }

    public void processAttack(EntityLivingBase entity, boolean packet) {
        float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.player.getHeldItemMainhand(), target.getCreatureAttribute());
        if (Mode.getValString().equalsIgnoreCase("Click")) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        } else {
            if (packet)
                Wrapper.INSTANCE.sendPacket(new CPacketUseEntity(target));
            else
                Wrapper.INSTANCE.attack(target);
            Wrapper.INSTANCE.swingArm();
            if (sharpLevel > 0.0f) {
                mc.player.onEnchantmentCritical(target);
            }
        }
    }

    void updateTarget() {
        RayTraceResult object = Wrapper.INSTANCE.mc().objectMouseOver;
        if (object == null) {
            return;
        }
        EntityLivingBase entity = null;
        if (this.target != entity) {
            this.target = null;
        }
        if (object.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (object.entityHit instanceof EntityLivingBase) {
                entity = (EntityLivingBase) object.entityHit;
                this.target = entity;
            }
        } else if (object.typeOfHit != RayTraceResult.Type.ENTITY && advanced.getValBoolean()) {
            entity = getClosestEntity();
        }
        if (entity != null) {
            this.target = entity;
        }
    }

    EntityLivingBase getClosestEntity() {
        EntityLivingBase closestEntity = null;
        for (Object o : mc.world.loadedEntityList) {
            if (o instanceof EntityLivingBase && !(o instanceof EntityArmorStand)) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (check(entity)) {
                    if (closestEntity == null || mc.player.getDistance(entity) < mc.player.getDistance(closestEntity)) {
                        closestEntity = entity;
                    }
                }
            }
        }
        return closestEntity;
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
        if (ValidUtils.isBot(entity)) {
            return false;
        }
        if (ValidUtils.isFriendEnemy(entity)) {
            return false;
        }
        if (advanced.getValBoolean()) {
            if (!ValidUtils.isInAttackFOV(entity, 50)) {
                return false;
            }
            if (!ValidUtils.isInAttackRange(entity, 4.7F)) {
                return false;
            }
        }
        if (!ValidUtils.pingCheck(entity)) {
            return false;
        }
        return mc.player.canEntityBeSeen(entity);
    }
}
