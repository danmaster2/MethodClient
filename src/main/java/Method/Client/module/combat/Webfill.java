package Method.Client.module.combat;


import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.module.combat.HoleFill.GetLocalPlayerPosFloored;

public class Webfill extends Module {

    Setting always_on = setmgr.add(new Setting("hole toggle", this, true));
    Setting rotate = setmgr.add(new Setting("hole rotate", this, true));
    Setting range = setmgr.add(new Setting("range", this, 4, 1, 6, true));

    int new_slot = -1;
    boolean sneak = false;

    public Webfill() {
        super("Webfill", Keyboard.KEY_NONE, Category.COMBAT, "Webfill");
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            new_slot = find_in_hotbar();
            if (new_slot == -1) {
                this.toggle();
            }

        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                sneak = false;
            }
        }
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (mc.player == null) return;

        if (always_on.getValBoolean()) {

            EntityPlayer target = find_closest_target();
            if (target == null) return;

            if (mc.player.getDistance(target) < range.getValDouble() && is_surround()) {
                int last_slot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = new_slot;
                mc.playerController.updateController();
                place_blocks(GetLocalPlayerPosFloored());
                mc.player.inventory.currentItem = last_slot;
            }

        } else {
            int last_slot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = new_slot;
            mc.playerController.updateController();
            place_blocks(GetLocalPlayerPosFloored());
            mc.player.inventory.currentItem = last_slot;
            this.toggle();
        }


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

    private int find_in_hotbar() {

        for (int i = 0; i < 9; ++i) {

            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack.getItem() == Item.getItemById(30)) {
                return i;
            }

        }
        return -1;
    }

    private boolean is_surround() {

        BlockPos player_block = GetLocalPlayerPosFloored();
        return mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.west()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.north()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block.south()).getBlock() != Blocks.AIR
                && mc.world.getBlockState(player_block).getBlock() == Blocks.AIR;

    }


    private void place_blocks(BlockPos pos) {

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            return;
        }

        if (!Utils.checkForNeighbours(pos)) {
            return;
        }


        for (EnumFacing side : EnumFacing.values()) {

            BlockPos neighbor = pos.offset(side);

            EnumFacing side2 = side.getOpposite();

            if (!Utils.canBeClicked(neighbor)) continue;

            if (Surrond.blackList.contains(mc.world.getBlockState(neighbor).getBlock())) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                sneak = true;
            }

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            if (rotate.getValBoolean()) {
                Utils.faceVectorPacketInstant(hitVec);
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);

            return;
        }

    }


}
