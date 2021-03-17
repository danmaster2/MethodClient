package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import java.util.Objects;

import static Method.Client.module.command.Give.updateFirstEmptySlot;

public class Head extends Command {
    public Head() {
        super("Head");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (!mc.player.isCreative()) {
                ChatUtils.warning("You must be in creative mode.");
            }
            if (args.length < 1) {
                ChatUtils.error("Invalid syntax.");
                return;
            }
            if (args.length > 1) {
                ChatUtils.warning("Too many arguments.");
            }
            ItemStack stack = mc.player.inventory.getCurrentItem();
            if (!stack.isEmpty() && Item.getIdFromItem(stack.getItem()) == 397 && stack.getMetadata() == 3) {
                stack.setTagInfo("SkullOwner", new NBTTagString(args[0]));
                updateSlot(36 + mc.player.inventory.currentItem, stack);
                ChatUtils.message("Head's owner changed to \2477" + args[0] + "\247e.");
                return;
            }
            ItemStack newStack = new ItemStack(Item.getItemById(397), 1, 3);
            newStack.setTagInfo("SkullOwner", new NBTTagString(args[0]));
            updateFirstEmptySlot(newStack);
            ChatUtils.message("Given head of player \2477" + args[0] + "\247e to \2477"
                    + mc.player.getName() + "\247e.");
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    public static void updateSlot(int slot, ItemStack stack) {
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketCreativeInventoryAction(slot, stack));
    }

    @Override
    public String getDescription() {
        return "Head to Hand";
    }

    @Override
    public String getSyntax() {
        return "Head <Player>";
    }
}