package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

import java.util.Objects;

public class Lore extends Command {
    public Lore() {
        super("Lore");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args.length < 1) {
                ChatUtils.error("Invalid syntax.");
                return;
            }
            ItemStack stack = mc.player.inventory.getCurrentItem();
            if (stack.isEmpty()) {
                ChatUtils.error("You must hold an item in your hand.");
                return;
            }
            StringBuilder lore = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; i++) {
                lore.append(" ").append(args[i]);
            }
            lore = new StringBuilder(lore.toString().replace('&', '\247').replace("\247\247", "&"));
            if (!mc.player.isCreative()) {
                ChatUtils.warning("You must be in creative mode.");
            }
            if (stack.hasTagCompound()) {
                NBTTagList lores;
                assert stack.getTagCompound() != null;
                stack.getTagCompound().getCompoundTag("display").getTag("Lore");
                lores = (NBTTagList) stack.getTagCompound().getCompoundTag("display").getTag("Lore");
                lores.appendTag(new NBTTagString(lore.toString()));
                NBTTagCompound display = new NBTTagCompound();
                display.setTag("Lore", lores);
                stack.getTagCompound().getCompoundTag("display").merge(display);
            } else {
                NBTTagList lores = new NBTTagList();
                lores.appendTag(new NBTTagString(lore.toString()));
                NBTTagCompound display = new NBTTagCompound();
                display.setTag("Lore", lores);
                stack.setTagInfo("display", display);
            }
            updateSlot(36 + mc.player.inventory.currentItem, stack);
            ChatUtils.message("Added lore \2477" + lore + "\247e to the item.");

        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    public static void updateSlot(int slot, ItemStack stack) {
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketCreativeInventoryAction(slot, stack));
    }

    @Override
    public String getDescription() {
        return "Adds Lore to and object";
    }

    @Override
    public String getSyntax() {
        return "Lore <Lore>  ";
    }
}