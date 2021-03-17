package Method.Client.utils.Screens.Override;

import Method.Client.utils.Screens.Screen;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;

public class ConnectingInsert extends Screen {
    boolean IsguiConnecting = false;
    double starttime;
    public static ServerData Currentserver;

    @Override
    public void DrawScreenEvent(GuiScreenEvent.DrawScreenEvent event) {
        if (event.getGui() instanceof GuiConnecting && IsguiConnecting) {
            ServerAddress serveraddress = ServerAddress.fromString(Currentserver.serverIP);
            event.getGui().drawCenteredString(event.getGui().fontRenderer, "Connecting to: " + Currentserver.serverIP + " Port: " + serveraddress.getPort(), event.getGui().width / 2, event.getGui().height / 2 - 10, 11184810);
            event.getGui().drawCenteredString(event.getGui().fontRenderer, "Time Taken: " + ChatFormatting.GOLD + (System.currentTimeMillis() - starttime) + ChatFormatting.RESET + " ms", event.getGui().width / 2, event.getGui().height / 2 - 30, 11184810);

        }
    }

    @Override
    public void GuiScreenEventInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiConnecting) {
            starttime = System.currentTimeMillis();
            IsguiConnecting = true;
            Currentserver = mc.getCurrentServerData();
        } else {
            IsguiConnecting = false;
        }
    }


}
