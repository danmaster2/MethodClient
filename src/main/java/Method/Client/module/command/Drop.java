package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.item.ItemStack;

public class Drop extends Command {
    public Drop() {
        super("Drop");
    }


    @Override
    public void runCommand(String s, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("all")) {
                ClickType t = ClickType.THROW;
                for (int var2 = 9; var2 < 45; ++var2) {
                    mc.playerController.windowClick(0, var2, 1, t, mc.player);
                }
            } else if (args[0].equalsIgnoreCase("Mob")) {
                if (mc.player.getRidingEntity() instanceof AbstractHorse && mc.player.openContainer instanceof ContainerHorseInventory) {
                    for (int i = 2; i < 17; ++i) {
                        final ItemStack itemStack = mc.player.openContainer.getInventory().get(i);
                        if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
                            mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(mc.player.openContainer.windowId, -999, 0, ClickType.PICKUP, mc.player);
                        }
                    }
                }
            }

            if (args[0].equalsIgnoreCase("hand")) {
                mc.player.dropItem(true);

            }
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "Drops items";
    }

    @Override
    public String getSyntax() {
        return "Drop <all/hand/Mob> ";
    }
}