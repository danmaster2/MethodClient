package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.item.ItemStack;

import static Method.Client.module.command.Nbt.updateSlot;

public class Rename extends Command {
    public Rename() {
        super("Rename");
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
            StringBuilder name = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; i++) {
                name.append(" ").append(args[i]);
            }
            name = new StringBuilder(name.toString().replace('&', '\247').replace("\247\247", "&"));
            if (!mc.player.isCreative()) {
                ChatUtils.warning("You must be in creative mode!");
            }
            stack.setStackDisplayName(name.toString());
            updateSlot(36 + mc.player.inventory.currentItem, stack);
            ChatUtils.message("Item's name changed to \2477" + name + "\247e.");

        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Rename item!";
    }

    @Override
    public String getSyntax() {
        return "Rename <Name>";
    }
}