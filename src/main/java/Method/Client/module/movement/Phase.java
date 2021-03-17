
package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.directionSpeed;
import static Method.Client.utils.Utils.isMoving;

public class Phase extends Module {

    /////////////////////
    Setting mode = setmgr.add(new Setting("Phase Mode", this, "Noclip", "Noclip", "Simple", "Destroy", "Glitch", "VClip",
            "NCPDEV", "HFC", "WinterLithe", "Sand", "Packet", "Skip", "Sneak", "Dpacket"));
    Setting speed = setmgr.add(new Setting("speed", this, 10, .1, 2, false));
    Setting packets = setmgr.add(new Setting("packets", this, 5, 1, 10, true));

    private int resetNext;
    public static boolean Finalsep = false;
    public int vanillastage;

    public Phase() {
        super("Phase", Keyboard.KEY_NONE, Category.MOVEMENT, "Phase through blocks");
    }

    ///////////////////
    @Override
    public void onEnable() {
        Finalsep = false;
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
        mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
        mc.player.noClip = true;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mode.getValString().equalsIgnoreCase("Dpacket") && side == Connection.Side.OUT) {
            if (mc.player.collidedHorizontally && packet instanceof CPacketPlayer.Position) {
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.08D, mc.player.posZ, mc.player.onGround));
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.08D, mc.player.posZ, mc.player.onGround));
                double var2 = -Math.sin(Math.toRadians(mc.player.rotationYaw));
                double var4 = Math.cos(Math.toRadians(mc.player.rotationYaw));
                double var6 = (double) mc.player.movementInput.moveForward * var2 + (double) mc.player.movementInput.moveStrafe * var4;
                double var8 = (double) mc.player.movementInput.moveForward * var4 - (double) mc.player.movementInput.moveStrafe * var2;
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX + var6, mc.player.posY, mc.player.posZ + var8, false));
                return false;
            }

        }
        return !(packet instanceof CPacketPlayer) || packet instanceof CPacketPlayer.Position || !mode.getValString().equalsIgnoreCase("noclip");
    }

    @Override
    public void onDisable() {
        this.vanillastage = 0;
        Finalsep = false;
        mc.player.motionY = 0.0D;
        Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
        mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
        mc.player.noClip = false;
        mc.player.capabilities.isFlying = false;
    }


    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (mode.getValString().equalsIgnoreCase("Simple")) {
            MC.player.noClip = true;
            MC.player.fallDistance = 0f;

            MC.player.onGround = false;

            MC.player.jumpMovementFactor = 0.32f;

            if (MC.gameSettings.keyBindJump.isKeyDown()) MC.player.motionY += 0.32f;
            if (MC.gameSettings.keyBindSneak.isKeyDown()) MC.player.motionY -= 0.32f;
        }
        if (mode.getValString().equalsIgnoreCase("Sneak")) {
            mc.player.fallDistance = 0;
            mc.player.onGround = true;

            if (mc.gameSettings.keyBindJump.isPressed()) {
                mc.player.setVelocity(mc.player.motionX, 0.1, mc.player.motionZ);
            } else if (mc.gameSettings.keyBindSneak.isPressed()) {
                mc.player.addVelocity(0, -0.1, 0);
            } else {
                mc.player.setVelocity(mc.player.motionX, 0, mc.player.motionZ);
            }

        }
        if (mode.getValString().equalsIgnoreCase("noclip")) {
            if (mc.player != null) {
                mc.player.noClip = false;
            }
            if (mc.world != null && event.getEntity() == mc.player) {
                mc.player.noClip = true;
                mc.player.onGround = false;
                mc.player.fallDistance = 0;
            }
        }
        if (mode.getValString().equalsIgnoreCase("SAND")) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (mc.player.getRidingEntity() != null && mc.player.getRidingEntity() instanceof EntityBoat) {
                    final EntityBoat boat = (EntityBoat) mc.player.getRidingEntity();
                    if (boat.onGround) {
                        boat.motionY = 0.42f;
                    }
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("PACKET")) {

            final Vec3d dir = direction(mc.player.rotationYaw);
            if (mc.player.onGround && mc.player.collidedHorizontally) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.00001f, mc.player.posY, mc.player.posZ + dir.z * 0.0001f, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 2.0f, mc.player.posY, mc.player.posZ + dir.z * 2.0f, mc.player.onGround));
            }
        }
        if (mode.getValString().equalsIgnoreCase("SKIP")) {
            final Vec3d dir = direction(mc.player.rotationYaw);
            if (mc.player.onGround && mc.player.collidedHorizontally) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.001f, mc.player.posY + 0.1f, mc.player.posZ + dir.z * 0.001f, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.03f, 0, mc.player.posZ + dir.z * 0.03f, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + dir.x * 0.06f, mc.player.posY, mc.player.posZ + dir.z * 0.06f, mc.player.onGround));
            }
        }

        double var1;
        if (mode.getValString().equalsIgnoreCase("NCPDEV")) {
            mc.player.noClip = true;
            mc.player.setPosition(mc.player.posX, mc.player.posY - 1.0E-5D, mc.player.posZ);
            mc.player.motionY = -10.0D;
        }
        if (mode.getValString().equalsIgnoreCase("Glitch")) {
            mc.player.noClip = true;
            EnumFacing var4 = mc.player.getHorizontalFacing();
            mc.player.motionY = 0.0D;
            if (mc.player.movementInput.jump) {
                mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
            }

            if (mc.player.movementInput.sneak) {
                mc.player.setPosition(mc.player.posX, mc.player.posY - 1.0E-5D, mc.player.posZ);
                mc.player.motionY = -1000.0D;
            }

            if (isMoving(mc.player)) {
                if (var4.getName().equals(EnumFacing.NORTH.getName())) {
                    mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ - 0.001D);
                    if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
                        mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ - 0.2D);
                        final double[] directionSpeedVanilla = directionSpeed(1.5D);
                        mc.player.motionX = directionSpeedVanilla[0];
                        mc.player.motionZ = directionSpeedVanilla[1];
                    }
                }

                if (var4.getName().equals(EnumFacing.SOUTH.getName())) {
                    mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ + 0.001D);
                    if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
                        mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ + 0.2D);
                        final double[] directionSpeedVanilla = directionSpeed(1.5D);
                        mc.player.motionX = directionSpeedVanilla[0];
                        mc.player.motionZ = directionSpeedVanilla[1];
                    }
                }

                if (var4.getName().equals(EnumFacing.WEST.getName())) {
                    mc.player.setPositionAndUpdate(mc.player.posX - 0.001D, mc.player.posY, mc.player.posZ);
                    if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
                        mc.player.setPositionAndUpdate(mc.player.posX - 0.2D, mc.player.posY, mc.player.posZ);
                        final double[] directionSpeedVanilla = directionSpeed(1.5D);
                        mc.player.motionX = directionSpeedVanilla[0];
                        mc.player.motionZ = directionSpeedVanilla[1];
                    }
                }

                if (var4.getName().equals(EnumFacing.EAST.getName())) {
                    mc.player.setPositionAndUpdate(mc.player.posX + 0.001D, mc.player.posY, mc.player.posZ);
                    if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
                        mc.player.setPositionAndUpdate(mc.player.posX + 0.2D, mc.player.posY, mc.player.posZ);
                        final double[] directionSpeedVanilla = directionSpeed(1.5D);
                        mc.player.motionX = directionSpeedVanilla[0];
                        mc.player.motionZ = directionSpeedVanilla[1];
                    }
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("Sand")) {
            var1 = mc.player.posX;
            double y = mc.player.posY - 3.0D;
            double z = mc.player.posZ;
            if (this.vanillastage == 0) {
                int i;
                for (i = 0; i < 100; ++i) {
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(var1, mc.player.posY - 1.0001D, z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                    mc.player.setPosition(var1, mc.player.posY - 1.0001D, z);
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                }

                for (i = 0; i < 10; ++i) {
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(var1, y, z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                    mc.player.setPosition(var1, y, z);
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(var1, y, z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
                    mc.player.setPosition(var1, y, z);
                }

                ++this.vanillastage;
            }

            if (this.vanillastage > 0) {
                mc.player.noClip = true;
                Finalsep = true;
            }
        }

        var1 = -speed.getValDouble();
        EnumFacing var4;
        int i;
        if (mode.getValString().equalsIgnoreCase("HFC")) {
            final double[] directionSpeedVanilla = directionSpeed(speed.getValDouble());
            mc.player.motionX = directionSpeedVanilla[0];
            mc.player.motionZ = directionSpeedVanilla[1];
            var4 = mc.player.getHorizontalFacing();
            if (isMoving(mc.player)) {
                if (!mc.player.onGround && !isInsideBlock() && mc.player.isSneaking()) {
                    mc.player.motionY = 0.0D;
                } else {
                    mc.player.motionY = 100000.0D;
                }

                if (mc.player.ticksExisted % 2 == 0) {
                    if (var4.getName().equals(EnumFacing.NORTH.getName())) {
                        mc.player.getEntityBoundingBox().offset(0.0D, 0.0D, -3.0D);
                    }

                    if (var4.getName().equals(EnumFacing.SOUTH.getName())) {
                        mc.player.getEntityBoundingBox().offset(0.0D, 0.0D, 3.0D);
                    }

                    if (var4.getName().equals(EnumFacing.WEST.getName())) {
                        mc.player.getEntityBoundingBox().offset(-3.0D, 0.0D, 0.0D);
                    }

                    if (var4.getName().equals(EnumFacing.EAST.getName())) {
                        mc.player.getEntityBoundingBox().offset(3.0D, 0.0D, 0.0D);
                    }

                    for (i = 0; i < (int) packets.getValDouble(); ++i) {
                        if (var4.getName().equals(EnumFacing.NORTH.getName())) {
                            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ - 3.0D, true));
                        }

                        if (var4.getName().equals(EnumFacing.SOUTH.getName())) {
                            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ + 3.0D, true));
                        }

                        if (var4.getName().equals(EnumFacing.WEST.getName())) {
                            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX - 3.0D, mc.player.posY, mc.player.posZ, true));
                        }

                        if (var4.getName().equals(EnumFacing.EAST.getName())) {
                            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX + 3.0D, mc.player.posY, mc.player.posZ, true));
                        }
                    }
                } else {
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(5000.0D, mc.player.posY, 5000.0D, true));
                }
            }

            mc.player.noClip = true;
        } else if (mode.getValString().equalsIgnoreCase("WinterLithe")) {
            if (isInsideBlock() && mc.gameSettings.keyBindJump.pressed || !isInsideBlock() && mc.player.getCollisionBoundingBox() != null && mc.player.getCollisionBoundingBox().maxY > mc.player.getCollisionBoundingBox().minY && mc.player.isSneaking() && mc.player.collidedHorizontally) {
                --this.resetNext;
                double xOff;
                double zOff;
                double mx = Math.cos(Math.toRadians(mc.player.rotationYaw + 90.0F));
                double mz = Math.sin(Math.toRadians(mc.player.rotationYaw + 90.0F));
                xOff = (double) mc.player.moveForward * 1.2D * mx + (double) mc.player.moveForward * 1.2D * mz;
                zOff = (double) mc.player.moveForward * 1.2D * mz - (double) mc.player.moveForward * 1.2D * mx;
                if (isInsideBlock()) {
                    this.resetNext = 1;
                }

                if (this.resetNext > 0) {
                    mc.player.getEntityBoundingBox().offset(xOff, 0.0D, zOff);
                }
            }
        } else if (mode.getValString().equalsIgnoreCase("Vclip")) {
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.player.noClip = true;
                mc.player.setPosition(mc.player.posX, mc.player.posY + speed.getValDouble(), mc.player.posZ);
                mc.player.motionY = speed.getValDouble();
            }

            if (mc.gameSettings.keyBindSneak.pressed) {
                mc.player.noClip = true;
                mc.player.setPosition(mc.player.posX, mc.player.posY + var1, mc.player.posZ);
                mc.player.motionY = var1;
            }
        } else if (mode.getValString().equalsIgnoreCase("MotionY")) {
            if (isMoving(mc.player)) {
                var4 = mc.player.getHorizontalFacing();
                if (var4.getName().equals(EnumFacing.NORTH.getName())) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ - 3.0D);
                }

                if (var4.getName().equals(EnumFacing.SOUTH.getName())) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ + 3.0D);
                }

                if (var4.getName().equals(EnumFacing.WEST.getName())) {
                    mc.player.setPosition(mc.player.posX - 3.0D, mc.player.posY, mc.player.posZ);
                }

                if (var4.getName().equals(EnumFacing.EAST.getName())) {
                    mc.player.setPosition(mc.player.posX + 3.0D, mc.player.posY, mc.player.posZ);
                }

                mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
                mc.player.motionY = -1000.0D;
                mc.player.noClip = true;
                final double[] directionSpeedVanilla = directionSpeed(speed.getValDouble());
                mc.player.motionX = directionSpeedVanilla[0];
                mc.player.motionZ = directionSpeedVanilla[1];
            }
        }

        if (mode.getValString().equalsIgnoreCase("destroy")) {
            double[] dir = Utils.directionSpeed(1);
            if (mc.player.collidedHorizontally) {
                mc.world.destroyBlock(new BlockPos(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1]), false);
                mc.world.destroyBlock(new BlockPos(mc.player.posX + dir[0], mc.player.posY + 1, mc.player.posZ + dir[1]), false);
            }
            if (isMoving(mc.player) && mc.player.onGround) {
                final double[] directionSpeedVanilla = directionSpeed(0.23000000298023224);
                mc.player.motionX = directionSpeedVanilla[0];
                mc.player.motionZ = directionSpeedVanilla[1];
            }
        }
    }


    public boolean canPhase() {
        return !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava();
    }

    public static boolean isInsideBlock() {
        for (int x = (int) Objects.requireNonNull(mc.player.getCollisionBoundingBox()).minX; x < mc.player.getCollisionBoundingBox().maxX + 1; ++x) {
            for (int y = (int) mc.player.getCollisionBoundingBox().minY; y < mc.player.getCollisionBoundingBox().maxY + 1; ++y) {
                for (int z = (int) mc.player.getCollisionBoundingBox().minZ; z < mc.player.getCollisionBoundingBox().maxZ + 1; ++z) {
                    Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox;
                        if ((boundingBox = block.getCollisionBoundingBox(mc.world.getBlockState(new BlockPos(x, y, z)), mc.world, new BlockPos(x, y, z))) != null && mc.player.getCollisionBoundingBox().intersects(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Vec3d direction(float yaw) {
        return new Vec3d(Math.cos(degToRad(yaw + 90f)), 0, Math.sin(degToRad(yaw + 90f)));
    }

    public static double degToRad(double deg) {
        return deg * (float) (Math.PI / 180.0f);
    }


}
