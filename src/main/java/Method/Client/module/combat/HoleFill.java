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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static Method.Client.Main.setmgr;

public class HoleFill extends Module {

    Setting hole_toggle = setmgr.add(new Setting("hole toggle", this, true));
    Setting hole_rotate = setmgr.add(new Setting("hole rotate", this, true));
    Setting hole_range = setmgr.add(new Setting("hole range", this, 4, 1, 6, true));
    Setting swing = setmgr.add(new Setting("swing", this, "Mainhand", "Mainhand", "Offhand", "Both", "None"));

    private final ArrayList<BlockPos> holes = new ArrayList<>();

    public HoleFill() {
        super("HoleFill", Keyboard.KEY_NONE, Category.COMBAT, "HoleFill");
    }

    @Override
    public void onEnable() {
        if (find_in_hotbar() == -1) {
            this.toggle();
        }
        find_new_holes();
    }

    @Override
    public void onDisable() {
        holes.clear();
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {


        if (find_in_hotbar() == -1) {
            this.toggle();
            return;
        }

        if (!hole_toggle.getValBoolean()) {
            this.toggle();
            return;

        } else {
            find_new_holes();
        }

        BlockPos pos_to_fill = null;

        for (BlockPos pos : new ArrayList<>(holes)) {

            if (pos == null) continue;

            Utils.ValidResult result = Utils.valid(pos);

            if (result != Utils.ValidResult.Ok) {
                holes.remove(pos);
                continue;
            }


            pos_to_fill = pos;
            break;
        }

        int old_slot = -1;
        if (find_in_hotbar() != mc.player.inventory.currentItem) {
            old_slot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = find_in_hotbar();
        }

        if (pos_to_fill != null) {
            if (Utils.placeBlock(pos_to_fill, hole_rotate.getValBoolean(), swing)) {
                holes.remove(pos_to_fill);
            }
        }
        if (old_slot != -1)
            mc.player.inventory.currentItem = old_slot;

    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;
                do {
                    float f = sphere ? (float) cy + r : (float) (cy + h);
                    if (!((float) y < f)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < (double) (r * r)) || hollow && dist < (double) ((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                } while (true);
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static BlockPos GetLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public void find_new_holes() {

        holes.clear();

        for (BlockPos pos : getSphere(GetLocalPlayerPosFloored(), (float) hole_range.getValDouble(), (int) hole_range.getValDouble(), false, true, 0)) {

            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            boolean possible = true;

            for (BlockPos seems_blocks : new BlockPos[]{
                    new BlockPos(0, -1, 0),
                    new BlockPos(0, 0, -1),
                    new BlockPos(1, 0, 0),
                    new BlockPos(0, 0, 1),
                    new BlockPos(-1, 0, 0)
            }) {
                Block block = mc.world.getBlockState(pos.add(seems_blocks)).getBlock();

                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    possible = false;
                    break;
                }
            }

            if (possible) {
                holes.add(pos);
            }
        }
    }

    private int find_in_hotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockEnderChest) {
                    return i;
                }

                if (block instanceof BlockObsidian) {
                    return i;
                }
            }
        }
        return -1;
    }


}
