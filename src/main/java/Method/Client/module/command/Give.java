package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import java.util.Objects;

public class Give extends Command {
    public Give() {
        super("Give");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (!mc.player.isCreative())
                ChatUtils.error("You must be in creative mode.");

            Item item = null;
            int amount = 1;
            int metadata = 0;
            StringBuilder nbt = null;
            item = Item.getByNameOrId(args[0]);
            if (item == null) {
                ChatUtils.error("There's no such item with name \2477" + args[0] + "\247c.");
                return;
            }

            if (args.length > 1) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NullPointerException | NumberFormatException e) {
                    ChatUtils.error("\2477" + args[1] + "\247c is not a valid number.");
                    return;
                }
                // metadata
                if (args.length > 2) {
                    try {
                        metadata = Integer.parseInt(args[2]);
                    } catch (NullPointerException | NumberFormatException e) {
                        ChatUtils.error("\2477" + args[2] + "\247c is not a valid number.");
                        return;
                    }
                    // nbt
                    if (args.length > 3) {
                        nbt = new StringBuilder(args[3]);
                        for (int i = 4; i < args.length; i++)
                            nbt.append(" ").append(args[i]);
                        nbt = new StringBuilder(nbt.toString().replace('&', '\247').replace("\247\247", "&"));
                    }
                }
            }
            // generate item stack
            ItemStack stack = new ItemStack(item, amount, metadata);
            // apply nbt data
            if (nbt != null) {
                try {
                    stack.setTagCompound(JsonToNBT.getTagFromJson(nbt.toString()));
                } catch (NBTException e) {
                    ChatUtils.error("Data tag parsing failed: " + e.getMessage());
                    return;
                }
            }
            // give item stack
            updateFirstEmptySlot(stack);

        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Gives items";
    }

    public static void updateFirstEmptySlot(ItemStack stack) {
        int slot = 0;
        boolean slotFound = false;
        for (int i = 0; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                slot = i;
                slotFound = true;
                break;
            }
        }
        if (!slotFound) {
            ChatUtils.warning("Could not find empty slot. Operation has been aborted.");
            return;
        }

        int convertedSlot = slot;
        if (slot < 9)
            convertedSlot += 36;

        if (stack.getCount() > 64) {
            ItemStack passStack = stack.copy();
            stack.setCount(64);
            passStack.setCount(passStack.getCount() - 64);
            mc.player.inventory.setInventorySlotContents(slot, stack);
            Objects.requireNonNull(mc.getConnection())
                    .sendPacket(new CPacketCreativeInventoryAction(convertedSlot, stack));
            updateFirstEmptySlot(passStack);
            return;
        }

        (Objects.requireNonNull(mc.getConnection())).sendPacket(new CPacketCreativeInventoryAction(convertedSlot, stack));
    }

    @Override
    public String getSyntax() {
        return "Give <Id> <MetaData>";
    }
}