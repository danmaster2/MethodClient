package Method.Client.module.movement;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.isMoving;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class Scaffold extends Module {

    BlockPos blockDown1 = null;
    BlockPos blockDown2 = null;
    BlockPos blockDown3 = null;
    Setting Towermode = setmgr.add(new Setting("Towermode", this, "Tower", "Tower", "Onjump", "Timer", "ACC",
            "NCP", "Spartan", "TP", "Long", "None"));
    Setting Placecolor = setmgr.add(new Setting("Placecolor", this, 0, 1, 1, .22));
    Setting TimerVal = setmgr.add(new Setting("TimerVal", this, 1, 0, 3, false, Towermode, "Timer", 8));
    Setting radius = setmgr.add(new Setting("Radius", this, 0, 0, 5, true));
    Setting Sprint = setmgr.add(new Setting("Sprint place", this, false));
    Setting Towerspeed = setmgr.add(new Setting("Towerspeed", this, 1, 0, 1, false, Towermode, "Tower", 8));
    Setting TowerDelay = setmgr.add(new Setting("TowerDelay", this, 100, 0, 1000, true, Towermode, "Tower", 9));
    Setting TowerFeet = setmgr.add(new Setting("Tower Feet Look", this, true));
    Setting SneakPlace = setmgr.add(new Setting("SneakPlace", this, true));
    Setting DrawMode = setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));

    private final TimerUtils timer = new TimerUtils();


    public Scaffold() {
        super("Scaffold", Keyboard.KEY_NONE, Category.MOVEMENT, "Scaffolds");
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = (float) (50);
        super.onDisable();
    }

    @Override
    public void onEnable() {
        mc.timer.tickLength = (float) (50);
        super.onEnable();
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {

        final int newSlot = findSlotWithBlock();
        if (newSlot != -1)
            Custom(newSlot);
        else {
            ChatUtils.error("No blocks found in hotbar!");
            this.toggle();
        }

        if (TowerFeet.getValBoolean() && !Towermode.getValString().equalsIgnoreCase("None"))
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90, mc.player.onGround));

        if (Towermode.getValString().equalsIgnoreCase("Tower") && timer.isDelay((long) TowerDelay.getValDouble()) && mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = -0.28f;
            final float towerMotion = 0.41999998688f;
            mc.player.setVelocity(0, towerMotion * Towerspeed.getValDouble(), 0);
            this.timer.setLastMS();
        }

        if (Towermode.getValString().equalsIgnoreCase("Onjump")) {
            mc.rightClickDelayTimer = 0;
            if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.motionY < 0) {
                mc.player.motionY *= 1.48D;
                if (mc.player.onGround) {
                    mc.player.setJumping(false);
                    mc.player.jump();
                }
            }
        }
        if (Towermode.getValString().equalsIgnoreCase("NCP")) {

            double blockBelow = -2.0D;
            if (mc.player.onGround && mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY = 0.41999998688697815D;
            }

            if (mc.player.motionY < 0.1D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof BlockAir)) {
                mc.player.motionY = -10.0D;
            }
        }
        if (Towermode.getValString().equalsIgnoreCase("TP")) {
            if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround) {
                mc.player.motionY -= 0.2300000051036477D;
                mc.player.setPosition(mc.player.posX, mc.player.posY + 1.1, mc.player.posZ);
            }
        }
        if (Towermode.getValString().equalsIgnoreCase("Spartan")) {
            double blockBelow = -2.0D;
            if (mc.player.onGround && mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY = 0.41999998688697815D;
            }

            if (mc.player.motionY < 0.0D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof BlockAir)) {
                mc.player.motionY = -10.0D;
            }
        }
        if (Towermode.getValString().equalsIgnoreCase("ACC")) {
            if (mc.player.onGround && mc.gameSettings.keyBindJump.pressed) {
                mc.player.motionY = 0.395D;
            }
            mc.player.motionY -= 0.002300000051036477D;
        }
        if (Towermode.getValString().equalsIgnoreCase("Long") && mc.gameSettings.keyBindJump.isKeyDown()) {
            if (isMoving(mc.player)) {
                if (isOnGround(0.76D) && !isOnGround(0.75D)) {
                    if (mc.player.motionY > 0.23D) {
                        if (mc.player.motionY < 0.25D) {
                            double round = (double) Math.round(mc.player.posY);
                            mc.player.motionY = round - mc.player.posY;
                        }
                    }
                }

                if (isOnGround(1.0E-4D)) {
                    mc.player.motionY = 0.42D;
                    mc.player.motionX *= 0.9D;
                    mc.player.motionZ *= 0.9D;
                } else {
                    if (mc.player.posY >= (double) Math.round(mc.player.posY) - 1.0E-4D) {
                        if (mc.player.posY <= (double) Math.round(mc.player.posY) + 1.0E-4D) {
                            mc.player.motionY = 0.0D;
                        }
                    }
                }
            } else {
                mc.player.motionX = 0.0D;
                mc.player.motionZ = 0.0D;
                mc.player.jumpMovementFactor = 0.0F;
                double x = mc.player.posX;
                double y = mc.player.posY - 1.0D;
                double z = mc.player.posZ;
                BlockPos blockBelow = new BlockPos(x, y, z);
                if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR) {
                    mc.player.motionY = 0.4196D;
                    mc.player.motionX *= 0.75D;
                    mc.player.motionZ *= 0.75D;
                }
            }
        }
        if (Towermode.getValString().equalsIgnoreCase("Timer")) {
            if (!mc.player.onGround)
                mc.timer.tickLength = (float) (50 / TimerVal.getValDouble());
            mc.rightClickDelayTimer = 0;
            if (mc.player.onGround) {
                mc.player.motionY = 0.3932d;
                mc.timer.tickLength = (float) (50);
            }
        }

    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public void XZmodify(double x, double z) {
        mc.player.motionX = x;
        mc.player.motionZ = z;
    }

    private void Custom(int NewSlot) {
        int StartingItem = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = NewSlot;

        if (mc.gameSettings.keyBindSprint.isKeyDown() && this.Sprint.getValBoolean()) {
            final float X = ((MathHelper.sin((float) Math.toRadians(mc.player.rotationYaw)) * 0.03f));
            final float Z = ((MathHelper.cos((float) Math.toRadians(mc.player.rotationYaw)) * 0.03f));
            if (mc.gameSettings.keyBindForward.isKeyDown())
                XZmodify(-X, Z);
            if (mc.gameSettings.keyBindBack.isKeyDown())
                XZmodify(X, -Z);
            if (mc.gameSettings.keyBindLeft.isKeyDown())
                XZmodify(X, Z);
            if (mc.gameSettings.keyBindRight.isKeyDown())
                XZmodify(-X, -Z);

            blockDown1 = new BlockPos(mc.player).down(2);

            if (mc.world.getBlockState(blockDown1).getMaterial().isReplaceable()) {
                Blockplace(EnumHand.MAIN_HAND, blockDown1);
            }
            if (Math.abs(mc.player.motionX) > 0.03 && mc.world.getBlockState(new BlockPos(blockDown1.getX() + mc.player.motionX / Math.abs(mc.player.motionX), blockDown1.getY() - 1, blockDown1.getZ())).getMaterial().isReplaceable()) {
                Blockplace(EnumHand.MAIN_HAND, new BlockPos(blockDown1.getX() + mc.player.motionX / Math.abs(mc.player.motionX), blockDown1.getY() - 1, blockDown1.getZ()));
            } else if (Math.abs(mc.player.motionZ) > 0.03 && mc.world.getBlockState(new BlockPos(blockDown1.getX(), blockDown1.getY() - 1, blockDown1.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ))).getMaterial().isReplaceable()) {
                Blockplace(EnumHand.MAIN_HAND, new BlockPos(blockDown1.getX(), blockDown1.getY() - 1, blockDown1.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ)));
            }
            return;
        }
        if (this.radius.getValDouble() == 0) {
            blockDown2 = new BlockPos(mc.player).down();
            if (mc.world.getBlockState(blockDown2).getMaterial().isReplaceable() && !mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(blockDown2).expand(.05, .05, .05))) {
                Blockplace(EnumHand.MAIN_HAND, blockDown2);
            }
            if (Math.abs(mc.player.motionX) > 0.033 && mc.world.getBlockState(new BlockPos(blockDown2.getX() + mc.player.motionX / Math.abs(mc.player.motionX), blockDown2.getY(), blockDown2.getZ())).getMaterial().isReplaceable()) {
                Blockplace(EnumHand.MAIN_HAND, new BlockPos(blockDown2.getX() + mc.player.motionX / Math.abs(mc.player.motionX), blockDown2.getY(), blockDown2.getZ()));
            } else if (Math.abs(mc.player.motionZ) > 0.033 && mc.world.getBlockState(new BlockPos(blockDown2.getX(), blockDown2.getY(), blockDown2.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ))).getMaterial().isReplaceable()) {
                Blockplace(EnumHand.MAIN_HAND, new BlockPos(blockDown2.getX(), blockDown2.getY(), blockDown2.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ)));
            }
            return;
        }

        final ArrayList<BlockPos> WidePlace = new ArrayList<>();
        for (int i = (int) -this.radius.getValDouble(); i <= this.radius.getValDouble(); ++i) {
            for (int j = (int) -this.radius.getValDouble(); j <= this.radius.getValDouble(); ++j) {
                WidePlace.add(new BlockPos(mc.player.posX + i, mc.player.posY - 1.0, mc.player.posZ + j));
            }
        }
        for (final BlockPos blockPos3 : WidePlace) {
            if (mc.world.getBlockState(blockPos3).getMaterial().isReplaceable()) {
                blockDown3 = blockPos3;
                Blockplace(EnumHand.MAIN_HAND, blockPos3);
            }
        }

        mc.player.inventory.currentItem = StartingItem;
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            if (blockDown1 != null)
                RenderBlock(DrawMode.getValString(), Standardbb(blockDown1), Placecolor.getcolor(), LineWidth.getValDouble());
            if (blockDown2 != null && (this.radius.getValDouble() == 0))
                RenderBlock(DrawMode.getValString(), Standardbb(blockDown2), Placecolor.getcolor(), LineWidth.getValDouble());
            if (blockDown3 != null)
                RenderBlock(DrawMode.getValString(), Standardbb(blockDown3), Placecolor.getcolor(), LineWidth.getValDouble());
        } catch (Exception ignored) {
        }
    }


    public int findSlotWithBlock() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBlock) {
                Block block = Block.getBlockFromItem(stack.getItem()).getDefaultState().getBlock();
                if (block.isFullBlock(BlockUtils.getBlock(new BlockPos(mc.player).down()).getDefaultState()) && block != Blocks.SAND && block != Blocks.GRAVEL) {
                    return i;
                }
            }
        }
        return -1;
    }


    public void Blockplace(final EnumHand enumHand, final BlockPos blockPos) {
        final Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (final EnumFacing enumFacing : EnumFacing.values()) {
            final BlockPos offset = blockPos.offset(enumFacing);
            final EnumFacing opposite = enumFacing.getOpposite();
            if (Utils.canBeClicked(offset)) {
                final Vec3d Vec3d = new Vec3d(offset).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                if (vec3d.squareDistanceTo(Vec3d) <= 18.0625) {
                    final float[] array = Utils.getNeededRotations(Vec3d, 0, 0);
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(array[0], array[1], mc.player.onGround));
                    if (SneakPlace.getValBoolean())
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    mc.playerController.processRightClickBlock(mc.player, mc.world, offset, opposite, Vec3d, enumHand);
                    mc.player.swingArm(enumHand);
                    if (SneakPlace.getValBoolean())
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    return;
                }
            }
        }
    }

}

