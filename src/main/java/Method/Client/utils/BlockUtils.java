package Method.Client.utils;


import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.LinkedList;

public final class BlockUtils {
    private static final Minecraft mc = Wrapper.INSTANCE.mc();

    public static Material getMaterial(BlockPos pos) {
        return getState(pos).getMaterial();
    }

    static public LinkedList<BlockPos> blocks = new LinkedList<>();

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static float getHardness(BlockPos pos) {
        return getState(pos).getPlayerRelativeBlockHardness(Wrapper.INSTANCE.player(), Wrapper.INSTANCE.world(), pos);
    }


    public static boolean breakBlockSimple(BlockPos pos) {
        EnumFacing side = null;
        EnumFacing[] sides = EnumFacing.values();

        Vec3d eyesPos = Utils.getEyesPos();
        Vec3d relCenter = getState(pos).getBoundingBox(Wrapper.INSTANCE.world(), pos).getCenter();
        Vec3d center = new Vec3d(pos).add(relCenter);

        Vec3d[] hitVecs = new Vec3d[sides.length];
        for (int i = 0; i < sides.length; i++) {
            Vec3i dirVec = sides[i].getDirectionVec();
            Vec3d relHitVec = new Vec3d(relCenter.x * dirVec.getX(),
                    relCenter.y * dirVec.getY(),
                    relCenter.z * dirVec.getZ());
            hitVecs[i] = center.add(relHitVec);
        }

        for (int i = 0; i < sides.length; i++) {
            if (Wrapper.INSTANCE.world().rayTraceBlocks(eyesPos, hitVecs[i], false,
                    true, false) != null)
                continue;

            side = sides[i];
            break;
        }

        if (side == null) {
            double distanceSqToCenter = eyesPos.squareDistanceTo(center);
            for (int i = 0; i < sides.length; i++) {
                if (eyesPos.squareDistanceTo(hitVecs[i]) >= distanceSqToCenter)
                    continue;

                side = sides[i];
                break;
            }
        }

        if (side == null)
            side = sides[0];

        Utils.faceVectorPacket(hitVecs[side.ordinal()]);

        if (!Wrapper.INSTANCE.controller().onPlayerDamageBlock(pos, side))
            return false;
        Wrapper.INSTANCE.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

        return true;
    }


    public static void breakBlocksPacketSpam(Iterable<BlockPos> blocks) {
        Vec3d eyesPos = Utils.getEyesPos();
        NetHandlerPlayClient connection = Wrapper.INSTANCE.player().connection;

        for (BlockPos pos : blocks) {
            Vec3d posVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
            double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

            for (EnumFacing side : EnumFacing.values()) {
                Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));

                if (eyesPos.squareDistanceTo(hitVec) >= distanceSqPosVec)
                    continue;

                connection.sendPacket(new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
                connection.sendPacket(new CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));

                break;
            }
        }
    }

}
