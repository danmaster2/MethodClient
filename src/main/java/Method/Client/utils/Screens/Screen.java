package Method.Client.utils.Screens;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Screen {
    protected static Minecraft mc = Minecraft.getMinecraft();

    public boolean visible = true;

    public TextFormatting ColorfromInt(int i) {
        if (i < 16)
            return TextFormatting.fromColorIndex(i);

        switch (i) {
            case 16:
                return TextFormatting.OBFUSCATED;
            case 17:
                return TextFormatting.BOLD;
            case 18:
                return TextFormatting.STRIKETHROUGH;
            case 19:
                return TextFormatting.UNDERLINE;
            case 20:
                return TextFormatting.ITALIC;
            case 21:
                return TextFormatting.RESET;
        }
        return null;
    }

    public void setup() {
    }

    public void GuiOpen(GuiOpenEvent event) {
    }

    public void GuiScreenEventPost(GuiScreenEvent.ActionPerformedEvent.Post event) {
    }

    public void GuiScreenEventInit(GuiScreenEvent.InitGuiEvent.Post event) {
    }



    public void GuiScreenEventPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
    }

    public void DrawScreenEvent(GuiScreenEvent.DrawScreenEvent event) {
    }

    public void onWorldUnload(WorldEvent.Unload event) {
    }
}
