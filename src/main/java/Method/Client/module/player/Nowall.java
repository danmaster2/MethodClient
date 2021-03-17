package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;


public class Nowall extends Module {
    public Nowall() {
        super("Nowall", Keyboard.KEY_NONE, Category.PLAYER, "Click through walls");
    }

    Setting Storage = setmgr.add(new Setting("Storage", this, true));
    Setting Mine = setmgr.add(new Setting("Mine", this, false));

    private boolean clicked;
    private boolean focus = false;

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Mine.getValBoolean()) {
            mc.world.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityLivingBase)
                    .filter(entity -> mc.player == entity)
                    .map(entity -> (EntityLivingBase) entity)
                    .filter(entity -> !(entity.isDead))
                    .forEach(this::process);

            RayTraceResult normal_result = mc.objectMouseOver;

            if (normal_result != null) {
                focus = normal_result.typeOfHit == RayTraceResult.Type.ENTITY;
            }
        }

    }

    private void process(EntityLivingBase event) {
        RayTraceResult bypass_entity_result = event.rayTrace(6, mc.getRenderPartialTicks());
        if (bypass_entity_result != null && focus) {
            if (bypass_entity_result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos block_pos = bypass_entity_result.getBlockPos();
                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    mc.playerController.onPlayerDamageBlock(block_pos, EnumFacing.UP);
                }
            }
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT && Storage.getValBoolean())
            if (packet instanceof CPacketPlayerTryUseItemOnBlock) {
                if (this.clicked) {
                    this.clicked = false;
                    return true;
                }
                final CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock) packet;
                if (mc.currentScreen == null) {
                    final Block block = mc.world.getBlockState(packet2.getPos()).getBlock();
                    final BlockPos usable = findUsableBlock(packet2.getHand(), packet2.getDirection(), packet2.getFacingX(), packet2.getFacingY(), packet2.getFacingZ());
                    if (block.onBlockActivated(mc.world, packet2.getPos(), mc.world.getBlockState(packet2.getPos()), mc.player, packet2.getHand(), packet2.getDirection(), packet2.getFacingX(), packet2.getFacingY(), packet2.getFacingZ())) {
                        return true;
                    }
                    if (usable != null) {
                        mc.player.swingArm(packet2.getHand());
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(usable, packet2.getDirection(), packet2.getHand(), packet2.getFacingX(), packet2.getFacingY(), packet2.getFacingZ()));
                        this.clicked = true;
                    } else {
                        final Entity usableEntity = findUsableEntity();
                        if (usableEntity != null) {
                            mc.player.connection.sendPacket(new CPacketUseEntity(usableEntity, packet2.getHand()));
                            this.clicked = true;
                        }
                    }
                }
            }
        return true;
    }

    private Entity findUsableEntity() {

        Entity entity = null;

        for (int i = 0; i <= mc.playerController.getBlockReachDistance(); i++) {
            final AxisAlignedBB bb = this.traceToBlock(i, mc.getRenderPartialTicks());
            float maxDist = mc.playerController.getBlockReachDistance();
            for (Entity e : mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, bb)) {
                float currentDist = mc.player.getDistance(e);
                if (currentDist <= maxDist) {
                    entity = e;
                    maxDist = currentDist;
                }
            }
        }
        return entity;
    }

    private BlockPos findUsableBlock(EnumHand hand, EnumFacing dir, float x, float y, float z) {
        for (int i = 0; i <= mc.playerController.getBlockReachDistance(); i++) {
            final AxisAlignedBB bb = this.traceToBlock(i, mc.getRenderPartialTicks());
            final BlockPos pos = new BlockPos(bb.minX, bb.minY, bb.minZ);
            final Block block = mc.world.getBlockState(pos).getBlock();
            if (block.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, hand, dir, x, y, z)) {
                return new BlockPos(pos);
            }
        }

        return null;
    }

    private AxisAlignedBB traceToBlock(double dist, float partialTicks) {
        final Vec3d pos = mc.player.getPositionEyes(partialTicks);
        final Vec3d angles = mc.player.getLook(partialTicks);
        final Vec3d end = pos.add(angles.x * dist, angles.y * dist, angles.z * dist);
        return new AxisAlignedBB(end.x, end.y, end.z, end.x + 1, end.y + 1, end.z + 1);
    }

}
