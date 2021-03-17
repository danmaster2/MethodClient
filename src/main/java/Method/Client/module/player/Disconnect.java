package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Disconnect extends Module {
    Setting leaveHealth = setmgr.add(new Setting("LeaveHealth", this, 4, 0, 20, true));
    Setting Totem = setmgr.add(new Setting("Totem", this, false));
    Setting Playersight = setmgr.add(new Setting("Player", this, false));


    public Disconnect() {
        super("Auto Disconnect", Keyboard.KEY_NONE, Category.PLAYER, "Disconnect on low health");
    }


    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mc.player.getHealth() <= leaveHealth.getValDouble()) {
            if (!Totem.getValBoolean())
                Quit();
            else if (Totemcount() < 1)
                Quit();
            this.toggle();
        }
    }

    public static int Totemcount() {
        int totem = 0;
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING))
            totem++;
        for (int i = 9; i <= 44; i++) {
            if (MC.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.TOTEM_OF_UNDYING)
                totem++;
        }
        return totem;
    }

    private void Quit() {
        boolean flag = Wrapper.INSTANCE.mc().isIntegratedServerRunning();
        mc.world.sendQuittingDisconnectingPacket();
        Wrapper.INSTANCE.mc().loadWorld(null);
        if (flag) {
            Wrapper.INSTANCE.mc().displayGuiScreen(new GuiMainMenu());
        } else {
            Wrapper.INSTANCE.mc().displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }


}
