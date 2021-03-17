package Method.Client.module.movement;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static Method.Client.Main.setmgr;

public class Glide extends Module {
    public Glide() {
        super("Glide", Keyboard.KEY_NONE, Category.MOVEMENT, "Glide");
    }


    Setting Mode = setmgr.add(new Setting("Mode", this, "Falling", "Falling", "Constant", "Flat", "ACC",
            "NCP", "Matrix", "Simple", "Randomadd"));
    Setting Damage = setmgr.add(new Setting("Damage", this, false));
    Setting fallSpeed = setmgr.add(new Setting("fallSpeed", this, .25, .005, .25, false, Mode, "Falling", 2));
    Setting moveSpeed = setmgr.add(new Setting("moveSpeed", this, 1, .5, 5, false));
    Setting minHeight = setmgr.add(new Setting("minHeight", this, 0, 0, 2, false, Mode, "Falling", 2));
    Setting ypos = setmgr.add(new Setting("ypos", this, 1, 1, 5, false, Mode, "Randomadd", 2));
    Setting ymotion = setmgr.add(new Setting("ymotion", this, 1, 1, 5, false, Mode, "Randomadd", 2));
    Setting MotionY = setmgr.add(new Setting("MotionY", this, 12, 0, 100, false, Mode, "Constant", 2));


    TimerUtils timer = new TimerUtils();

    @Override
    public void onEnable() {
        if (Damage.getValBoolean()) {
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6, mc.player.posZ, true));
            mc.player.motionX *= 0.2;
            mc.player.motionZ *= 0.2;
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (Mode.getValString().equalsIgnoreCase("Flat")) {
            mc.player.motionY = 0.19;
        }
        super.onEnable();
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        EntityPlayerSP player = mc.player;
        if (Mode.getValString().equalsIgnoreCase("Randomadd") && mc.player.motionY < -0.01 && !mc.player.onGround) {
            double firstrandom = Math.random() / ymotion.getValDouble() / 10;
            double secondrandom = Math.random() / ypos.getValDouble() / 10;
            if (ymotion.getValDouble() > 0)
                mc.player.motionY += firstrandom;
            if (ypos.getValDouble() > 0)
                mc.player.posY += secondrandom;
        }
        if (Mode.getValString().equalsIgnoreCase("Constant")) {
            mc.player.motionY = -MotionY.getValDouble() / 60;
        }
        if (Mode.getValString().equalsIgnoreCase("Simple")) {
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.setPosition(mc.player.posX, mc.player.posY + 0.01f, mc.player.posZ);
            }
            mc.player.motionY = -0.1f;
            {
                if (mc.gameSettings.keyBindSneak.pressed) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY - 0.5f, mc.player.posZ);
                }
            }
        }
        if (Mode.getValString().equalsIgnoreCase("Matrix")) {
            if (mc.player.fallDistance > 3) {
                if (mc.player.ticksExisted % 3 == 0) mc.player.motionY = -0.1;
                if (mc.player.ticksExisted % 4 == 0) mc.player.motionY = -0.2;
            }
        }
        if (Mode.getValString().equalsIgnoreCase("NCP")) {
            mc.player.onGround = true;
            mc.player.capabilities.isFlying = true;
            tpRel(mc.player.motionX, mc.player.motionY = -0.0222, mc.player.motionZ);
            tpPacket(mc.player.motionX, mc.player.motionY - 9, mc.player.motionZ);
        }
        if (Mode.getValString().equalsIgnoreCase("Flat")) {
            if (!player.capabilities.isFlying && player.fallDistance > 0.0f && !player.isSneaking()) {
                player.motionY = 0.0;
            }
            if (Wrapper.INSTANCE.mcSettings().keyBindSneak.isKeyDown()) {
                player.motionY = -0.11;
            }

            if (Wrapper.INSTANCE.mcSettings().keyBindJump.isKeyDown()) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                player.onGround = false;
            }
            if (timer.delay(50)) {
                player.onGround = false;
                timer.setLastMS();
            }
        }
        if (Mode.getValString().equalsIgnoreCase("ACC")) {
            if (mc.player.motionY < 0 && mc.player.isAirBorne || mc.player.onGround) {
                mc.player.motionY = -0.125f;
                mc.player.jumpMovementFactor *= 1.01227f;
                mc.player.noClip = true;
                mc.player.fallDistance = 0;
                mc.player.onGround = true;
                mc.player.moveStrafing += 0.8F * moveSpeed.getValDouble();
                mc.player.jumpMovementFactor += 0.2F;
                mc.player.velocityChanged = true;
            }
        } else if (Mode.getValString().equalsIgnoreCase("Falling")) {
            World world = mc.world;

            if (!player.isAirBorne || player.isInWater() || player.isInLava()
                    || player.isOnLadder() || player.motionY >= 0)
                return;

            if (minHeight.getValDouble() > 0) {
                AxisAlignedBB box = player.getEntityBoundingBox();
                box = box.union(box.offset(0, -minHeight.getValDouble(), 0));
                if (world.collidesWithAnyBlock(box))
                    return;

                BlockPos min =
                        new BlockPos(new Vec3d(box.minX, box.minY, box.minZ));
                BlockPos max =
                        new BlockPos(new Vec3d(box.maxX, box.maxY, box.maxZ));
                Stream<BlockPos> stream = StreamSupport
                        .stream(BlockPos.getAllInBox(min, max).spliterator(), true);

                // manual collision check, since liquids don't have bounding boxes
                if (stream.map(BlockUtils::getBlock)
                        .anyMatch(b -> b instanceof BlockLiquid))
                    return;
            }

            player.motionY = Math.max(player.motionY, -fallSpeed.getValDouble());
            player.jumpMovementFactor *= moveSpeed.getValDouble();
        }
        super.onClientTick(event);
    }

    public static void tpRel(double x, double y, double z) {
        EntityPlayerSP player = mc.player;
        player.setPosition(player.posX + x, player.posY + y, player.posZ + z);
    }

    public static void tpPacket(double x, double y, double z) {
        EntityPlayerSP player = mc.player;
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(player.posX + x, player.posY + y, player.posZ + z, false));
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(player.posX, player.posY, player.posZ, false));
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(player.posX, player.posY, player.posZ, true));
    }
}
