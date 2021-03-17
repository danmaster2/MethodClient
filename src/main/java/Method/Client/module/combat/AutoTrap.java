package Method.Client.module.combat;


import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Method.Client.Main.setmgr;

public class AutoTrap extends Module {
    public AutoTrap() {
        super("AutoTrap", Keyboard.KEY_NONE, Category.COMBAT, "AutoTrap");
    }


    public Setting place_mode = setmgr.add(new Setting("cage", this, "Extra", "Extra", "Face", "Normal", "Feet"));
    public Setting blocks_per_tick = setmgr.add(new Setting("blocks Per Tick", this, 4, 0, 8, true));
    public Setting rotate = setmgr.add(new Setting("rotate", this, false));
    public Setting chad_mode = setmgr.add(new Setting("Modify mode", this, false));
    public Setting range = setmgr.add(new Setting("range", this, 5.5, 3.5, 10, false));
    public Setting Hand = setmgr.add(new Setting("Hand", this, "Mainhand", "Mainhand", "Offhand", "Both", "None"));

    private String last_tick_target_name = "";

    private int offset_step = 0;
    private int timeout_ticker = 0;
    private int y_level;

    private boolean first_run = true;


    @Override
    public void onEnable() {
        timeout_ticker = 0;
        y_level = (int) Math.round(mc.player.posY);
        first_run = true;
        if (find_obi_in_hotbar() == -1) {
            this.toggle();
        }
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        int timeout_ticks = 20;
        if (timeout_ticker > timeout_ticks && chad_mode.getValBoolean()) {
            timeout_ticker = 0;
            this.toggle();
            return;
        }

        EntityPlayer closest_target = find_closest_target();

        if (closest_target == null) {
            this.toggle();
            return;
        }

        if (chad_mode.getValBoolean() && (int) Math.round(mc.player.posY) != y_level) {
            this.toggle();

            return;
        }

        if (first_run) {
            first_run = false;
            last_tick_target_name = closest_target.getName();

        } else if (!last_tick_target_name.equals(closest_target.getName())) {

            last_tick_target_name = closest_target.getName();
            offset_step = 0;

        }

        final List<Vec3d> place_targets = new ArrayList<Vec3d>();

        if (place_mode.getValString().equalsIgnoreCase("Normal")) {
            Collections.addAll(place_targets, offsets_default);
        } else if (place_mode.getValString().equalsIgnoreCase("Extra")) {
            Collections.addAll(place_targets, offsets_extra);
        } else if (place_mode.getValString().equalsIgnoreCase("Feet")) {
            Collections.addAll(place_targets, offsets_feet);
        } else {
            Collections.addAll(place_targets, offsets_face);
        }

        int blocks_placed = 0;

        while (blocks_placed < blocks_per_tick.getValDouble()) {

            if (offset_step >= place_targets.size()) {
                offset_step = 0;
                break;
            }

            final BlockPos offset_pos = new BlockPos(place_targets.get(offset_step));
            final BlockPos target_pos = new BlockPos(closest_target.getPositionVector()).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());

            int old_slot = -1;
            if (find_obi_in_hotbar() != mc.player.inventory.currentItem) {
                old_slot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = find_obi_in_hotbar();
            }
            if (Utils.trytoplace(target_pos))
                if (Utils.placeBlock(target_pos, rotate.getValBoolean(), Hand)) {
                    ++blocks_placed;
                }

            if (old_slot != -1)
                mc.player.inventory.currentItem = old_slot;
            offset_step++;
        }

        timeout_ticker++;
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

    public EntityPlayer find_closest_target() {

        if (mc.world.playerEntities.isEmpty())
            return null;

        EntityPlayer closestTarget = null;

        for (final EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;

            if (FriendManager.isFriend(target.getName()))
                continue;

            if (!Utils.isLiving(target))
                continue;

            if (target.getHealth() <= 0.0f)
                continue;

            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;

            closestTarget = target;
        }

        return closestTarget;
    }


    private final Vec3d[] offsets_default = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsets_face = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsets_feet = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 1.0),
            new Vec3d(1.0, 3.0, 0.0),
            new Vec3d(-1.0, 3.0, 0.0),
            new Vec3d(0.0, 3.0, 0.0)
    };

    private final Vec3d[] offsets_extra = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0),
            new Vec3d(0.0, 3.0, 0.0),
            new Vec3d(0.0, 4.0, 0.0)
    };


}
