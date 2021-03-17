
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.render.Trail;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class HitEffects extends Module {

    /////////////////////

    public HitEffects() {
        super("HitEffects", Keyboard.KEY_NONE, Category.MISC, "Effects on Hit");
    }

    Setting Lightning = setmgr.add( new Setting("Lightning", this, false));
    Setting Sounds = setmgr.add(new Setting("Sounds", this, false));
    Setting Mode = setmgr.add(new Setting("Mode", this, "SMOKE", "HEART", "FIREWORK", "FLAME", "CLOUD",
            "WATER", "LAVA", "SLIME", "EXPLOSION", "MAGIC", "REDSTONE", "SWORD"));
    Setting Yoffset = setmgr.add(new Setting("YPos offset", this, 0, 0, 2, false));


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof CPacketUseEntity) {
            final CPacketUseEntity packet2 = (CPacketUseEntity) packet;
            if (packet2.getAction() == CPacketUseEntity.Action.ATTACK) {
                Entity entity = ((CPacketUseEntity) packet).getEntityFromWorld(mc.world);
                if (entity != null && !entity.isDead) {
                    if (Lightning.getValBoolean()) {
                        mc.world.spawnEntity(new EntityLightningBolt(mc.world, entity.posX, entity.posY, entity.posZ, true));
                    }
                    if (Sounds.getValBoolean()) {
                        final SoundEvent thunderSound = new SoundEvent(new ResourceLocation("minecraft", "entity.lightning.thunder"));
                        final SoundEvent lightningImpactSound = new SoundEvent(new ResourceLocation("minecraft", "entity.lightning.impact"));
                        mc.world.playSound(mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), thunderSound, SoundCategory.WEATHER, 1.0f, 1.0f);
                        mc.world.playSound(mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), lightningImpactSound, SoundCategory.WEATHER, 1.0f, 1.0f);
                    }
                    for (int i = 0; i < 5; i++) {
                        Trail.Renderparticle((EntityLivingBase) entity, Mode.getValString(), Yoffset.getValDouble());
                    }
                }
            }
        }

        return true;
    }
}
