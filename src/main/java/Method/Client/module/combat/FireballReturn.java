package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class FireballReturn extends Module {


    Setting yaw = setmgr.add(new Setting("yaw", this, 25, 0, 50, false));
    Setting pitch = setmgr.add(new Setting("pitch", this, 25, 0, 50, false));
    Setting range = setmgr.add(new Setting("range", this, 10, .1, 10, false));

    public EntityFireball target;
    public TimerUtils timer = new TimerUtils();

    public FireballReturn() {
        super("FireballReturn", Keyboard.KEY_NONE, Category.COMBAT, "Returns Fireballs to sender");
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        updateTarget();
        attackTarget();
        super.onClientTick(event);
    }

    void updateTarget() {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityFireball) {
                EntityFireball entity = (EntityFireball) object;
                if (isInAttackRange(entity) && !entity.isDead && !entity.onGround && entity.canBeAttackedWithItem()) {
                    target = entity;
                }
            }
        }
    }

    void attackTarget() {
        if (target == null) {
            return;
        }
        Utils.getNeededRotations(this.target.getPositionVector(), (float) yaw.getValDouble(), (float) pitch.getValDouble());
        int currentCPS = Utils.random(4, 7);
        if (timer.isDelay(1000 / currentCPS)) {
            mc.clickMouse();
            timer.setLastMS();
            target = null;
        }
    }

    public boolean isInAttackRange(EntityFireball entity) {
        return entity.getDistance(mc.player) <= this.range.getValDouble();
    }

}
