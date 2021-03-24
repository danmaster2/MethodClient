package Method.Client.module.combat;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

import static Method.Client.Main.setmgr;

public class Surround extends Module {

    Setting blocksPerTick = setmgr.add(new Setting("blocksPerTick", this, 10, 0, 10, true));
    Setting rotate = setmgr.add(new Setting("rotate", this, true));
    Setting autoCenter = setmgr.add(new Setting("autoCenter", this, true));
    Setting offInAir = setmgr.add(new Setting("offInAir", this, true));
    Setting BypassCenter = setmgr.add(new Setting("Bypass AutoCenter", this, true, autoCenter, 2));
    Setting Hand = setmgr.add(new Setting("Hand", this, "Mainhand", "Mainhand", "Offhand", "Both", "None"));
    Setting Onerun = setmgr.add(new Setting("Run Once", this, true));

    private int playerHotbarSlot;
    private int lastHotbarSlot;
    private int offsetStep;
    public static List<Block> blackList;
    public static List<Block> shulkerList;
    private static Vec3d[] SURROUND;

    public Surround() {
        super("Surround", Keyboard.KEY_NONE, Category.COMBAT, "Surrounds you with obsidian");
    }

    @Override
    public void setup() {
        blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
        SURROUND = new Vec3d[]{new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0)};
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
        offsetStep = 0;
    }

    @Override
    public void onEnable() {
        if (autoCenter.getValBoolean()) {
            if (BypassCenter.getValBoolean()) {
                double lMotionX = (Math.floor(mc.player.posX) + .5) - mc.player.posX;
                double lMotionZ = (Math.floor(mc.player.posZ) + .5) - mc.player.posZ;
                mc.player.motionX = lMotionX / 2;
                mc.player.motionZ = lMotionZ / 2;
            } else
                centerPlayer(Math.floor(mc.player.posX) + .5, mc.player.posY, Math.floor(mc.player.posZ) + .5);
        }
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }

    private void centerPlayer(final double x, final double y, final double z) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (offInAir.getValBoolean() && !mc.player.onGround) {
            this.toggle();
        }
        int blocksPlaced = 0;
        while (blocksPlaced < blocksPerTick.getValDouble()) {
            if (offsetStep >= SURROUND.length) {
                offsetStep = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(SURROUND[offsetStep]);
            final BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

            int old_slot = -1;
            if (find_obi_in_hotbar() != mc.player.inventory.currentItem) {
                old_slot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = find_obi_in_hotbar();
            }
            if (Utils.trytoplace(targetPos))
                if (Utils.placeBlock(targetPos, rotate.getValBoolean(), Hand)) {
                    if (Utils.placeBlock(targetPos, rotate.getValBoolean(), Hand)) {
                        ++blocksPlaced;
                    }
                }
            if (old_slot != -1)
                mc.player.inventory.currentItem = old_slot;
            ++offsetStep;

            if (blocksPlaced > 0) {
                if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                    mc.player.inventory.currentItem = playerHotbarSlot;
                    lastHotbarSlot = playerHotbarSlot;
                }
            }
            if (blocksPlaced == 0) {
                this.toggle();
            }
        }
        if (Onerun.getValBoolean())
            this.toggle();
    }

    private int find_obi_in_hotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockEnderChest)
                    return i;

                else if (block instanceof BlockObsidian)
                    return i;

            }
        }
        return -1;
    }

}
