package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Burrow extends Module {

    Setting mode = setmgr.add(new Setting("Mode", this, "JUMP", "JUMP", "GLITCH", "TP"));

    Setting glitchY = setmgr.add(new Setting("glitchY", this, .5, 0.1, 1.5, false, mode, "GLITCH", 1));
    Setting tpHeight = setmgr.add(new Setting("tpHeight", this, .5, 0, 10, false, mode, "TP", 1));
    Setting delay = setmgr.add(new Setting("delay", this, 200, 1, 500, false));
    Setting Rotate = setmgr.add(new Setting("Rotate", this, true));
    Setting Instant = setmgr.add(new Setting("Instant", this, true));
    Setting Center = setmgr.add(new Setting("Center", this, true));
    Setting CenterBypass = setmgr.add(new Setting("CenterBypass", this, true, Center, 5));
    Setting OffGround = setmgr.add(new Setting("OffGround", this, true));


    private final TimerUtils timer = new TimerUtils();

    public Burrow() {
        super("Burrow", Keyboard.KEY_NONE, Category.COMBAT, "Burrow into hole");
    }

    private int find_obi_in_hotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian)
                    return i;

            }
        }
        return -1;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.timer.isDelay((long) this.delay.getValDouble())) {
            if (find_obi_in_hotbar() != -1) {
                // get our hand swap context and ensure we have obsidian

                double posy = mc.player.posY;

                int current = mc.player.inventory.currentItem;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(find_obi_in_hotbar()));
                mc.player.inventory.currentItem = find_obi_in_hotbar();
                final BlockPos positionToPlaceAt = new BlockPos(mc.player.getPositionVector()).down();
                if (this.place(positionToPlaceAt, mc)) { // we've attempted to place the block
                    if (this.OffGround.getValBoolean()) {
                        mc.player.onGround = false; // set onground to false
                    }
                    switch (this.mode.getValString()) {
                        case "JUMP":
                            mc.player.jump(); // attempt another jump to flag ncp
                            break;
                        case "GLITCH":
                            mc.player.motionY = this.glitchY.getValDouble();
                            break;
                        case "TP":
                            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - this.tpHeight.getValDouble(), mc.player.posZ, mc.player.onGround));
                            break;
                    }
                }

                mc.player.connection.sendPacket(new CPacketHeldItemChange(current));
                mc.player.inventory.currentItem = current;
                if (this.Instant.getValBoolean())
                    mc.player.posY = posy;
                this.toggle(); // toggle off the module
            }
        }
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            if (find_obi_in_hotbar() != -1) {
                // attempt to center
                if (Center.getValBoolean()) {
                    if (CenterBypass.getValBoolean()) {
                        double lMotionX = (Math.floor(mc.player.posX) + .5) - mc.player.posX;
                        double lMotionZ = (Math.floor(mc.player.posZ) + .5) - mc.player.posZ;
                        mc.player.motionX = lMotionX / 2;
                        mc.player.motionZ = lMotionZ / 2;
                    } else {
                        final double[] newPos = {Math.floor(mc.player.posX) + 0.5d, mc.player.posY, Math.floor(mc.player.posZ) + 0.5d};
                        final CPacketPlayer.Position middleOfPos = new CPacketPlayer.Position(newPos[0], newPos[1], newPos[2], mc.player.onGround);
                        if (!mc.world.isAirBlock(new BlockPos(newPos[0], newPos[1], newPos[2]).down())) {
                            if (mc.player.posX != middleOfPos.x && mc.player.posZ != middleOfPos.z) {
                                mc.player.connection.sendPacket(middleOfPos);
                                mc.player.setPosition(newPos[0], newPos[1], newPos[2]);
                            }
                        }
                    }
                }

                mc.player.jump(); // jump
                this.timer.setLastMS(); // start timer
            } else {
                ChatUtils.message("You dont have obsidian to use");
                this.toggle(); // toggle off
            }
        }
    }

    private EnumFacing calcSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos sideOffset = pos.offset(side);
            IBlockState offsetState = mc.world.getBlockState(sideOffset);
            if (!offsetState.getBlock().canCollideCheck(offsetState, false)) continue;
            if (!offsetState.getMaterial().isReplaceable()) return side;
        }
        return null;
    }

    private boolean place(final BlockPos pos, final Minecraft mc) {
        final Block block = mc.world.getBlockState(pos).getBlock();

        final EnumFacing direction = this.calcSide(pos);
        if (direction == null)
            return false;

        final boolean activated = block.onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, direction, 0, 0, 0);

        if (activated)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        final EnumFacing otherSide = direction.getOpposite();
        final BlockPos sideOffset = pos.offset(direction);

        if (Rotate.getValBoolean()) {
            final float[] angle = Utils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f));
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
        }

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(sideOffset, otherSide, EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

        if (activated)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

        return true;
    }

}
