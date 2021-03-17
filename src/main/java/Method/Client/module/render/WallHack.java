package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.Executer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import static Method.Client.Main.setmgr;

public class WallHack extends Module {

    public WallHack() {
        super("WallHack", Keyboard.KEY_NONE, Category.RENDER, "WallHack");
    }

    Setting players = setmgr.add(new Setting("players", this, false));
    Setting mobs = setmgr.add(new Setting("mobs", this, false));
    Setting Barrier = setmgr.add(new Setting("Barrier", this, false));

    TimerUtils timer = new TimerUtils();

    @Override
    public void onEnable() {
        Executer.init();
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (timer.isDelay(4500)) {
            if (Barrier.getValBoolean()) {
                Executer.execute(() -> {
                            Vec3i playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
                            for (int x = playerPos.getX() - 10; x < playerPos.getX() + 10; x++) {
                                for (int z = playerPos.getZ() - 10; z < playerPos.getZ() + 10; z++) {
                                    for (int y = (playerPos.getY() + 6); y > playerPos.getY() - 6; y--) {
                                        if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.BARRIER) {
                                            mc.world.spawnParticle(EnumParticleTypes.BARRIER, x + .5, y + .5, z + .5, 0.0D, 0.0D, 0.0D);
                                        }
                                    }
                                }
                            }
                        }
                );
            }
            timer.setLastMS();
        }
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        RenderHelper.enableStandardItemLighting();
        for (Object object : mc.world.loadedEntityList) {
            Entity entity = (Entity) object;
            this.render(entity, event.getPartialTicks());
        }
        super.onRenderWorldLast(event);
    }

    void render(Entity entity, float ticks) {
        Entity ent = getEntity(entity);
        if (ent == null || ent == mc.player) {
            return;
        }
        if (ent == mc.getRenderViewEntity() && Wrapper.INSTANCE.mcSettings().thirdPersonView == 0) {
            return;
        }
        mc.entityRenderer.disableLightmap();
        mc.getRenderManager().renderEntityStatic(ent, ticks, false);
        mc.entityRenderer.enableLightmap();
    }

    Entity getEntity(Entity e) {
        Entity entity = null;
        if (players.getValBoolean() && e instanceof EntityPlayer) {
            entity = e;
        } else if (mobs.getValBoolean() && e instanceof EntityLiving) {
            entity = e;
        } else if (e instanceof EntityItem) {
            entity = e;
        } else if (e instanceof EntityArrow) {
            entity = e;
        }
        return entity;
    }
}
