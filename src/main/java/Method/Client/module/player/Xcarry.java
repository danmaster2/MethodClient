package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static net.minecraft.network.play.client.CPacketEntityAction.Action.OPEN_INVENTORY;

public class Xcarry extends Module {

    public Xcarry() {
        super("Xcarry", Keyboard.KEY_NONE, Category.PLAYER, "Xcarry or SecretClose!");
    }

    Setting Packetclose=setmgr.add(new Setting("Fake close", this, false));


    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null) {
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (Packetclose.getValBoolean() && packet instanceof CPacketEntityAction) {
            CPacketEntityAction pac = (CPacketEntityAction) packet;
            if (pac.getAction().equals(OPEN_INVENTORY))
                return false;
        }

        return !(packet instanceof CPacketCloseWindow);
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.currentScreen instanceof GuiInventory) {
            mc.playerController.updateController();
        }
    }

}
