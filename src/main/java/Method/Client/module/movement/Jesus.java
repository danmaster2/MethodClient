
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Jesus extends Module {
    /////////////////////
    Setting mode = setmgr.add(new Setting("Mode", this, "Solid", "Solid", "BOUNCE", "FrostWalker", "BunnyHop", "Aac"));
    Setting offset = setmgr.add(new Setting("offset", this, .05, 0, .9, false));
    Setting Blockdist = setmgr.add(new Setting("Top Water", this, false, mode, "FrostWalker", 2));
    Setting NoDrown = setmgr.add(new Setting("NoDrown", this, false));

    public TimerUtils Delayer = new TimerUtils();

    int noDown;
    int start;
    int cooldownSpeed;

    public Jesus() {
        super("Jesus", Keyboard.KEY_NONE, Category.MOVEMENT, "Jesus, Walk on water");
    }


    @Override
    public void onEnable() {
        noDown = 0;
    }

    @Override
    public void onDisable() {
        noDown = 0;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (NoDrown.getValBoolean())
            return !(packet instanceof CPacketPlayerAbilities) || !canSave();
        else return true;
    }

    private boolean canSave() {
        boolean swinging = mc.player.isSwingInProgress;
        Vec3d prevmotion = new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
        boolean moving = (prevmotion.x != 0.0D) || (!mc.player.collidedVertically) || (mc.gameSettings.keyBindJump.isPressed()) || (prevmotion.z != 0.0D);
        mc.player.inWater = false;
        return (mc.player.isInWater()) && (!swinging) && (!moving);
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Aac")) {
            if (mc.player.isInWater()) {
                ++start;
                if (start < 4) {
                    return;
                }

                ++noDown;
                ++cooldownSpeed;
                mc.gameSettings.keyBindJump.pressed = noDown < 2;
                mc.gameSettings.keyBindJump.pressed = true;
                if ((float) noDown >= 3.5F) {
                    noDown = 0;
                }

                if (cooldownSpeed >= 3) {

                    mc.player.motionX *= 1.1699999570846558D;

                    mc.player.motionZ *= 1.1699999570846558D;
                    cooldownSpeed = 0;
                    mc.gameSettings.keyBindJump.pressed = false;
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("BunnyHop")) {
            if (mc.player.isInWater()) {
                mc.player.jumpMovementFactor = (float) .1;
                mc.player.motionY = 0.42D;
            }
        }
        if (mode.getValString().equalsIgnoreCase("FrostWalker") && Delayer.isDelay(200)) {
            for (BlockPos b : BlockPos.getAllInBox((int) mc.player.posX - 3, (int) mc.player.posY - 2, (int) mc.player.posZ - 3, (int) mc.player.posX + 3, (int) mc.player.posY + 2, (int) mc.player.posZ + 3)) {
                if (mc.world.getBlockState(b).getBlock() == Blocks.WATER || mc.world.getBlockState(b).getBlock() == Blocks.FLOWING_WATER || mc.world.getBlockState(b).getBlock() == Blocks.LAVA || mc.world.getBlockState(b).getBlock() == Blocks.FLOWING_LAVA) {
                    if (Blockdist.getValBoolean() && mc.world.getBlockState(b.up()).getBlock() == Blocks.AIR)
                        continue;

                    mc.world.setBlockState(b, Blocks.FROSTED_ICE.getDefaultState());
                    mc.world.scheduleUpdate(b, Blocks.FROSTED_ICE, MathHelper.getInt(mc.player.getRNG(), 6, 12));
                }
            }
            Delayer.setLastMS();
        }
        if (mode.getValString().equalsIgnoreCase("Solid")) {
            BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            Block block = mc.world.getBlockState(blockPos).getBlock();
            if (block.getBlockState().getBlock() == Blocks.WATER || block.getBlockState().getBlock() == Blocks.FLOWING_WATER || block.getBlockState().getBlock() == Blocks.LAVA || block.getBlockState().getBlock() == Blocks.FLOWING_LAVA || mc.player.isInWater()) {
                mc.player.motionY = 0.0D;
                mc.player.jumpMovementFactor = (float) .1;
                if (mc.player.ticksExisted % 2 == 0) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.8471E-6D, mc.player.posZ);
                } else {
                    mc.player.setPosition(mc.player.posX, mc.player.posY - 2.8471E-6D, mc.player.posZ);
                }

                mc.player.onGround = true;
            }
        }
        super.onClientTick(event);
        if (mode.getValString().equalsIgnoreCase("BOUNCE")) {
            if (!mc.player.isSneaking() && !mc.player.noClip
                    && !mc.gameSettings.keyBindJump.isKeyDown() && isOnLiquid(offset.getValDouble()))
                mc.player.motionY = 0.1f;
        }
    }


    public static boolean isOnLiquid(double offset) {

        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }

        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -offset, 0.0d) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(0.0d, -offset, 0.0d);
        boolean onLiquid = false;
        int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;

    }
}
