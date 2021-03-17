package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Trail extends Module {

    Setting Self = setmgr.add(new Setting("Self", this, true));
    Setting Player = setmgr.add(new Setting("Player", this, true));
    Setting Mob = setmgr.add(new Setting("Mob", this, false));
    Setting Hostile = setmgr.add(new Setting("Hostile", this, false));
    Setting Tickrate = setmgr.add(new Setting("Per Sec", this, 10, 2, 20, true));
    Setting Yoffset = setmgr.add(new Setting("Y Offset", this, 0, 0, 2, false));

    Setting Trail = setmgr.add(new Setting("Mode", this, "SMOKE", "HEART", "FIREWORK", "FLAME", "CLOUD",
            "WATER", "LAVA", "SLIME", "EXPLOSION", "MAGIC", "REDSTONE", "SWORD"));


    public Trail() {
        super("Trail", Keyboard.KEY_NONE, Category.RENDER, "Trail");
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.player.ticksExisted % Tickrate.getValDouble() == 0) {
            for (Entity object : mc.world.loadedEntityList) {
                if (object instanceof EntityLivingBase) {
                    EntityLivingBase entity = (EntityLivingBase) object;
                    if (entity instanceof IAnimals && Mob.getValBoolean()) {
                        Renderparticle(entity, Trail.getValString(), Yoffset.getValDouble());
                    }

                    if (entity instanceof IMob && Hostile.getValBoolean()) {
                        Renderparticle(entity, Trail.getValString(), Yoffset.getValDouble());
                    }

                    if (entity instanceof EntityPlayer && Player.getValBoolean() && entity != mc.player) {
                        Renderparticle(entity, Trail.getValString(), Yoffset.getValDouble());
                    }
                    if (entity == mc.player && Self.getValBoolean()) {
                        Renderparticle(entity, Trail.getValString(), Yoffset.getValDouble());
                    }

                }
            }
        }

    }

    public static void Renderparticle(EntityLivingBase entity, String s, double yoffset) {
        try {
            switch (s) {
                case "HEART":
                    mc.world.spawnParticle(EnumParticleTypes.HEART, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "SWORD":
                    mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    mc.world.spawnParticle(EnumParticleTypes.CRIT, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    mc.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    mc.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "REDSTONE":
                    mc.world.spawnParticle(EnumParticleTypes.REDSTONE, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "MAGIC":
                    mc.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "LAVA":
                    mc.world.spawnParticle(EnumParticleTypes.LAVA, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    mc.world.spawnParticle(EnumParticleTypes.DRIP_LAVA, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "SMOKE":
                    mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "CLOUD":
                    mc.world.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "FLAME":
                    mc.world.spawnParticle(EnumParticleTypes.FLAME, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "SLIME":
                    mc.world.spawnParticle(EnumParticleTypes.SLIME, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "EXPLOSION":
                    mc.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "WATER":
                    mc.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    mc.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);
                    return;
                case "FIREWORK":
                    mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, entity.posX, entity.posY + .01 + yoffset, entity.posZ, entity.motionX * .4, entity.motionY * .4, entity.motionZ * .4);

            }
        } catch (Exception ignored) {
        }
    }
}
