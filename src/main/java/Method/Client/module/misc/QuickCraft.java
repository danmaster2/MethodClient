
package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.server.SPacketSetSlot;
import org.lwjgl.input.Keyboard;

public class QuickCraft extends Module {

    /////////////////////

    public QuickCraft() {
        super("QuickCraft", Keyboard.KEY_NONE, Category.MISC, "Quick Craft");
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.IN) {
            if (packet instanceof SPacketSetSlot) {
                if (((SPacketSetSlot) packet).getSlot() == 0 && ((SPacketSetSlot) packet).getStack().getItem() != Items.AIR) {
                    if (mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiCrafting) {
                        mc.playerController.windowClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE, mc.player);
                        mc.playerController.updateController();
                    }
                }
            }
        }
        return true;
    }

}
