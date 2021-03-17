package Method.Client.utils.Screens.Override;

import Method.Client.Main;
import Method.Client.utils.Screens.Screen;
import Method.Client.utils.system.WorldDownloader;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;

public class EscInsert extends Screen {
    boolean Disconnect = false;
    ServerData lastserver;

    @Override
    public void GuiScreenEventPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            if (event.getButton().id == 1 && !(mc.currentScreen instanceof GuiYesNo)) {
                mc.displayGuiScreen(new GuiYesNo(event.getGui(), "Disconnect", "Are you sure?", 1));
                Disconnect = true;
                event.setCanceled(true);
            }
        }
    }


    @Override
    public void GuiScreenEventInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiIngameMenu) {
            event.getButtonList().add(new GuiButton(554, event.getGui().width / 2 - 150, event.getGui().height / 4 + 32, 50, 20, "Relog"));
            event.getButtonList().add(new GuiButton(555, event.getGui().width / 2 - 150, event.getGui().height / 4 + 56, 50, 20, "Download"));
            event.getButtonList().add(new GuiButton(556, event.getGui().width / 2 - 150, event.getGui().height / 4 + 80, 50, 20, "ClickGui"));
        }
    }

    @Override
    public void GuiScreenEventPost(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.getGui() instanceof GuiYesNo && Disconnect) {
            if (event.getButton().id == 0) {
                Disconnect = false;
                mc.world.sendQuittingDisconnectingPacket();
                mc.loadWorld(null);
                mc.displayGuiScreen(new GuiMainMenu());
            }
            if (event.getButton().id == 1) {
                mc.displayGuiScreen(new GuiIngameMenu());
            }
        }

        if (event.getGui() instanceof GuiIngameMenu) {
            if (event.getButton().id == 554 && !mc.isIntegratedServerRunning()) {
                lastserver = mc.getCurrentServerData();
                Disconnect = false;
                mc.world.sendQuittingDisconnectingPacket();
                mc.loadWorld(null);
                Wrapper.INSTANCE.mc().displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));

                ServerAddress serveraddress = ServerAddress.fromString(lastserver.serverIP);
                mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, lastserver.serverIP, serveraddress.getPort()));

            }
            if (event.getButton().id == 555) {
                if (!WorldDownloader.Saving){
                    WorldDownloader.start();
                }
                else
                    WorldDownloader.stop();
            }
            if (event.getButton().id == 556) {
                mc.displayGuiScreen(Main.ClickGui);
            }

        }
    }



}
