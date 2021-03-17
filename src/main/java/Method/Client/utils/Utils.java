package Method.Client.utils;

import Method.Client.managers.Setting;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Utils {
    public static Field pressed;
    private static Field modifiersField;

    public static float[] rotationsToBlock = null;
    private static final Random RANDOM = new Random();

    public static List<Block> emptyBlocks;
    public static List<Block> rightclickableBlocks;

    static {
        emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
        rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    public static double[] directionSpeed(double speed) {
        float forward = Wrapper.INSTANCE.mc().player.movementInput.moveForward;
        float side = Wrapper.INSTANCE.mc().player.movementInput.moveStrafe;
        float yaw = Wrapper.INSTANCE.mc().player.prevRotationYaw + (Wrapper.INSTANCE.mc().player.rotationYaw - Wrapper.INSTANCE.mc().player.prevRotationYaw) * Wrapper.INSTANCE.mc().getRenderPartialTicks();

        if (forward != 0) {
            if (side > 0)
                yaw += (forward > 0 ? -45 : 45);
            else if (side < 0)
                yaw += (forward > 0 ? 45 : -45);
            side = 0;
            if (forward > 0)
                forward = 1;
            else if (forward < 0)
                forward = -1;
        }

        final double sin = sin(Math.toRadians(yaw + 90));
        final double cos = cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{posX, posZ};
    }

    public static float[] getNeededRotations(Vec3d vec, float yaw, float pitch) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;

        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        final float rotationYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float rotationPitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[]{
                updateRotation(Wrapper.INSTANCE.player().rotationYaw, rotationYaw, yaw / 4),
                updateRotation(Wrapper.INSTANCE.player().rotationPitch, rotationPitch, pitch / 4)
        };
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0F;
        final double difZ = to.z - from.z;

        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static float updateRotation(float PlayerRotation, float Modified, float MaxValueAccepted) {
        float degrees = MathHelper.wrapDegrees(Modified - PlayerRotation);
        if (MaxValueAccepted != 0) {
            if (degrees > MaxValueAccepted) {
                degrees = MaxValueAccepted;
            }
            if (degrees < -MaxValueAccepted) {
                degrees = -MaxValueAccepted;
            }
        }
        return PlayerRotation + degrees;
    }

    public static boolean isBlockEmpty(BlockPos pos) {
        try {
            if (emptyBlocks.contains(Wrapper.mc.world.getBlockState(pos).getBlock())) {
                AxisAlignedBB box = new AxisAlignedBB(pos);
                for (Entity entity : Wrapper.mc.world.loadedEntityList) {
                    if (((entity instanceof EntityLivingBase) || box.intersects(entity.getEntityBoundingBox())))
                        return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean trytoplace(BlockPos target_pos) {
        boolean should_try_place = true;

        if (!Wrapper.mc.world.getBlockState(target_pos).getMaterial().isReplaceable())
            should_try_place = false;

        for (final Entity entity : Wrapper.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {

            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                should_try_place = false;
                break;
            }

        }
        return should_try_place;
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static boolean placeBlock(BlockPos pos, boolean rotate, Setting s) {
        if (isBlockEmpty(pos)) {
            EnumFacing[] facings = EnumFacing.values();
            for (EnumFacing f : facings) {
                Block neighborBlock = Wrapper.mc.world.getBlockState(pos.offset(f)).getBlock();
                Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D);

                if (!emptyBlocks.contains(neighborBlock) && Wrapper.mc.player.getPositionEyes(Wrapper.mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25D) {
                    float[] rot = new float[]{Wrapper.mc.player.rotationYaw, Wrapper.mc.player.rotationPitch};

                    if (rotate) {
                        final float[] array = Utils.getNeededRotations(vec, 0, 0);
                        Wrapper.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(array[0], array[1], Wrapper.mc.player.onGround));
                    }

                    if (rightclickableBlocks.contains(neighborBlock)) {
                        Wrapper.mc.player.connection.sendPacket(new CPacketEntityAction(Wrapper.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }

                    Wrapper.mc.playerController.processRightClickBlock(Wrapper.mc.player, Wrapper.mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                    if (rightclickableBlocks.contains(neighborBlock)) {
                        Wrapper.mc.player.connection.sendPacket(new CPacketEntityAction(Wrapper.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }

                    if (rotate) {
                        Wrapper.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], Wrapper.mc.player.onGround));
                    }


                    if (s.getValString().equalsIgnoreCase("Mainhand") || s.getValString().equalsIgnoreCase(("Both"))) {
                        Wrapper.mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                    if (s.getValString().equalsIgnoreCase(("Offhand")) || s.getValString().equalsIgnoreCase(("Both"))) {
                        Wrapper.mc.player.swingArm(EnumHand.OFF_HAND);
                    }

                    return true;
                }
            }

        }

        return false;
    }


    public enum ValidResult {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }

    public static ValidResult valid(BlockPos pos) {
        // There are no entities to block placement,
        if (!Wrapper.mc.world.checkNoEntityCollision(new AxisAlignedBB(pos)))
            return ValidResult.NoEntityCollision;

        if (!checkForNeighbours(pos))
            return ValidResult.NoNeighbors;

        IBlockState l_State = Wrapper.mc.world.getBlockState(pos);

        if (l_State.getBlock() == Blocks.AIR) {
            final BlockPos[] l_Blocks =
                    {pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};

            for (BlockPos l_Pos : l_Blocks) {
                IBlockState l_State2 = Wrapper.mc.world.getBlockState(l_Pos);

                if (l_State2.getBlock() == Blocks.AIR)
                    continue;

                for (final EnumFacing side : EnumFacing.values()) {
                    final BlockPos neighbor = pos.offset(side);

                    if (Wrapper.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(Wrapper.mc.world.getBlockState(neighbor), false)) {
                        return ValidResult.Ok;
                    }
                }
            }

            return ValidResult.NoNeighbors;
        }

        return ValidResult.AlreadyBlockThere;
    }

    public static boolean checkForNeighbours(final BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(final BlockPos blockPos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.offset(side);
            if (!Wrapper.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }


    public static int random(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }


    public static void faceVectorPacket(Vec3d vec) {
        float[] rotations = getNeededRotations(vec, 0, 0);
        EntityPlayerSP pl = Minecraft.getMinecraft().player;

        float preYaw = pl.rotationYaw;
        float prePitch = pl.rotationPitch;

        pl.rotationYaw = rotations[0];
        pl.rotationPitch = rotations[1];
        pl.onUpdateWalkingPlayer();


        pl.rotationYaw = preYaw;
        pl.rotationPitch = prePitch;
    }

    public static boolean isMoving(Entity e) {
        return e.motionX != 0.0 && e.motionZ != 0.0 && e.motionY != 0.0;
    }

    public static boolean isMovinginput() {
        return Wrapper.INSTANCE.mc().player.movementInput.moveForward != 0.0F || Wrapper.INSTANCE.mc().player.movementInput.moveStrafe != 0.0F;
    }

    public static boolean canBeClicked(final BlockPos pos) {
        return Wrapper.INSTANCE.world().getBlockState(pos).getBlock().canCollideCheck(Wrapper.INSTANCE.world().getBlockState(pos), false);
    }


    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }


    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) return false;
        if (e instanceof EntityAgeable || e instanceof EntityAmbientCreature || e instanceof EntitySquid)
            return true;
        return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
    }


    public static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.INSTANCE.player().posX, Wrapper.INSTANCE.player().posY + Wrapper.INSTANCE.player().getEyeHeight(), Wrapper.INSTANCE.player().posZ);
    }

    public static void faceVectorPacketInstant(final Vec3d vec) {
        Utils.rotationsToBlock = getNeededRotations(vec, 0, 0);
    }

    public static void teleportToPosition(double[] startPosition, double[] endPosition, double setOffset, double slack, boolean extendOffset, boolean onGround) {
        boolean wasSneaking = false;

        if (Wrapper.INSTANCE.player().isSneaking())
            wasSneaking = true;

        double startX = startPosition[0];
        double startY = startPosition[1];
        double startZ = startPosition[2];

        double endX = endPosition[0];
        double endY = endPosition[1];
        double endZ = endPosition[2];

        double distance = Math.abs(startX - startY) + Math.abs(startY - endY) + Math.abs(startZ - endZ);

        int count = 0;
        while (distance > slack) {
            distance = Math.abs(startX - endX) + Math.abs(startY - endY) + Math.abs(startZ - endZ);

            if (count > 120) {
                break;
            }

            double offset = extendOffset && (count & 0x1) == 0 ? setOffset + 0.15D : setOffset;

            double diffX = startX - endX;
            double diffY = startY - endY;
            double diffZ = startZ - endZ;

            final double min = Math.min(Math.abs(diffX), offset);
            if (diffX < 0.0D) {
                startX += min;
            }
            if (diffX > 0.0D) {
                startX -= min;
            }
            final double min2 = Math.min(Math.abs(diffY), offset);
            if (diffY < 0.0D) {
                startY += min2;
            }
            if (diffY > 0.0D) {
                startY -= min2;
            }
            double min1 = Math.min(Math.abs(diffZ), offset);
            if (diffZ < 0.0D) {
                startZ += min1;
            }
            if (diffZ > 0.0D) {
                startZ -= min1;
            }

            if (wasSneaking) {
                Wrapper.INSTANCE.sendPacket(new CPacketEntityAction(Wrapper.INSTANCE.player(), CPacketEntityAction.Action.STOP_SNEAKING));
            }
            Objects.requireNonNull(Wrapper.INSTANCE.mc().getConnection()).getNetworkManager().sendPacket(new CPacketPlayer.Position(startX, startY, startZ, onGround));
            count++;
        }

        if (wasSneaking) {
            Wrapper.INSTANCE.sendPacket(new CPacketEntityAction(Wrapper.INSTANCE.player(), CPacketEntityAction.Action.START_SNEAKING));
        }

    }

    public static boolean isBlockMaterial(BlockPos blockPos, Block block) {
        return Wrapper.INSTANCE.world().getBlockState(blockPos).getBlock() == Blocks.AIR;
    }


    public static String getPlayerName(EntityPlayer player) {
        player.getGameProfile();
        return player.getGameProfile().getName();
    }

    public static String getEntityNameColor(EntityLivingBase entity) {
        String name = entity.getDisplayName().getFormattedText();
        if (name.contains("\u00a7")) {
            if (name.contains("\u00a71")) {
                return "\u00a71";
            } else if (name.contains("\u00a72")) {
                return "\u00a72";
            } else if (name.contains("\u00a73")) {
                return "\u00a73";
            } else if (name.contains("\u00a74")) {
                return "\u00a74";
            } else if (name.contains("\u00a75")) {
                return "\u00a75";
            } else if (name.contains("\u00a76")) {
                return "\u00a76";
            } else if (name.contains("\u00a77")) {
                return "\u00a77";
            } else if (name.contains("\u00a78")) {
                return "\u00a78";
            } else if (name.contains("\u00a79")) {
                return "\u00a79";
            } else if (name.contains("\u00a70")) {
                return "\u00a70";
            } else if (name.contains("\u00a7e")) {
                return "\u00a7e";
            } else if (name.contains("\u00a7d")) {
                return "\u00a7d";
            } else if (name.contains("\u00a7a")) {
                return "\u00a7a";
            } else if (name.contains("\u00a7b")) {
                return "\u00a7b";
            } else if (name.contains("\u00a7c")) {
                return "\u00a7c";
            } else if (name.contains("\u00a7f")) {
                return "\u00a7f";
            }
        }
        return "null";
    }


    public static boolean checkScreen() {
        return !(Wrapper.INSTANCE.mc().currentScreen instanceof GuiContainer)
                && !(Wrapper.INSTANCE.mc().currentScreen instanceof GuiChat)
                && Wrapper.INSTANCE.mc().currentScreen == null;
    }


    public static float getPitch(Entity entity) {
        double y = entity.posY - Wrapper.INSTANCE.player().posY;
        y /= Wrapper.INSTANCE.player().getDistance(entity);
        double pitch = Math.asin(y) * 57.29577951308232;
        pitch = -pitch;
        return (float) pitch;
    }

    public static float getYaw(Entity entity) {
        double x = entity.posX - Wrapper.INSTANCE.player().posX;
        double z = entity.posZ - Wrapper.INSTANCE.player().posZ;
        double yaw = Math.atan2(x, z) * 57.29577951308232;
        yaw = -yaw;
        return (float) yaw;
    }


    public static float getDirection() {
        float var1 = Wrapper.INSTANCE.player().rotationYaw;
        if (Wrapper.INSTANCE.player().moveForward < 0.0F) {
            var1 += 180.0F;
        }
        float forward = 1.0F;
        if (Wrapper.INSTANCE.player().moveForward < 0.0F) {
            forward = -0.5F;
        } else if (Wrapper.INSTANCE.player().moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (Wrapper.INSTANCE.player().moveStrafing > 0.0F) {
            var1 -= 90.0F * forward;
        }
        if (Wrapper.INSTANCE.player().moveStrafing < 0.0F) {
            var1 += 90.0F * forward;
        }
        var1 *= 0.017453292F;
        return var1;
    }

    public static int getDistanceFromMouse(final EntityLivingBase entity) {
        final float[] neededRotations = getNeededRotations(entity.getPositionVector(), 0, 0);
        final float neededYaw = Wrapper.INSTANCE.player().rotationYaw - neededRotations[0];
        final float neededPitch = Wrapper.INSTANCE.player().rotationPitch - neededRotations[1];
        final float distanceFromMouse = MathHelper.sqrt(neededYaw * neededYaw + neededPitch * neededPitch * 2.0f);
        return (int) distanceFromMouse;
    }


}
