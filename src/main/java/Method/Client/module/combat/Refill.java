package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Refill extends Module {


    public Refill() {
        super("Refill", Keyboard.KEY_NONE, Category.COMBAT, "Refill");
    }

    Setting delay = setmgr.add(new Setting("delay", this, 5, 0, 10, true));
    Setting percentage = setmgr.add(new Setting("percentage", this, 50, 0, 100, false));
    Setting offHand = setmgr.add(new Setting("offHand", this, true));

    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (timer.isDelay((long) (delay.getValDouble() * 1000)))
            if (mc.currentScreen instanceof GuiInventory) {
                return;
            }

        int toRefill = getRefillable(mc.player);
        if (toRefill != -1) {
            refillHotbarSlot(toRefill);
        }
    }

    private int getRefillable(EntityPlayerSP player) {
        if (offHand.getValBoolean()) {
            if (player.getHeldItemOffhand().getItem() != Items.AIR
                    && player.getHeldItemOffhand().getCount() < player.getHeldItemOffhand().getMaxStackSize()
                    && (double) player.getHeldItemOffhand().getCount() / player.getHeldItemOffhand().getMaxStackSize() <= (percentage.getValDouble() / 100.0)) {
                return 45;
            }
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.getItem() != Items.AIR && stack.getCount() < stack.getMaxStackSize()
                    && (double) stack.getCount() / stack.getMaxStackSize() <= (percentage.getValDouble() / 100.0)) {
                return i;
            }
        }

        return -1;
    }

    private int getSmallestStack(EntityPlayerSP player, ItemStack itemStack) {
        if (itemStack == null) {
            return -1;
        }

        int minCount = itemStack.getMaxStackSize() + 1;
        int minIndex = -1;

        // i starts at 9 so that the hotbar is not checked
        for (int i = 9; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);

            if (stack.getItem() != Items.AIR
                    && stack.getItem() == itemStack.getItem()
                    && stack.getCount() < minCount) {

                minCount = stack.getCount();
                minIndex = i;
            }
        }

        return minIndex;
    }


    public void refillHotbarSlot(int slot) {
        ItemStack stack;
        if (slot == 45) { // Special case for offhand
            stack = mc.player.getHeldItemOffhand();
        } else {
            stack = mc.player.inventory.mainInventory.get(slot);
        }

        // If the slot is air it cant be refilled
        if (stack.getItem() == Items.AIR) {
            return;
        }

        // The slot can't be refilled if there is nothing to refill it with
        int biggestStack = getSmallestStack(mc.player, stack);
        if (biggestStack == -1) {
            return;
        }

        // Special case for offhand (can't use QUICK_CLICK)
        if (slot == 45) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP, mc.player);
            return;
        }

        int overflow = -1; // The slot a shift click will overflow to
        for (int i = 0; i < 9 && overflow == -1; i++) {
            if (mc.player.inventory.mainInventory.get(i).getItem() == Items.AIR) {
                overflow = i;
            }
        }

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.QUICK_MOVE, mc.player);

        // If the two stacks don't overflow when combined we don't have to move overflow
        if (overflow != -1 && mc.player.inventory.mainInventory.get(overflow).getItem() != Items.AIR) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, overflow, ClickType.SWAP, mc.player);
        }
    }


}
