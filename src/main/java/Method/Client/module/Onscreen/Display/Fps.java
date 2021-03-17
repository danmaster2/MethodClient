package Method.Client.module.Onscreen.Display;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.managers.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Fps extends Module {
    public Fps() {
        super("Fps", Keyboard.KEY_NONE, Category.ONSCREEN, "Fps");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting xpos;
    static Setting ypos;
    static Setting Shadow;
    static Setting Frame;
    static Setting Background;
    static Setting FontSize;

    @Override
    public void setup() {
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(Background = new Setting("Background", this, false));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth/2) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 70, -20, (mc.displayHeight/2)  + 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("FpsSET", true);

    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("FpsSET", false);
    }

    public static class FpsRUN extends PinableFrame {

        public FpsRUN() {
            super("FpsSET", new String[]{}, (int) ypos.getValDouble(),(int)xpos.getValDouble());
        }

        @Override
        public void setup() {
            GetSetup(this,xpos,ypos,Frame,FontSize);
        }

        @Override
        public void Ongui() {
            GetInit(this, xpos, ypos, Frame,FontSize);

        }

        @Override
        public void onRenderGameOverlay(Text event) {
            final String framerate = "FPS: " + Minecraft.getDebugFPS();
            if (Background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, framerate), this.y + 20, BgColor.getcolor());
            fontSelect(Frame, framerate, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());
            super.onRenderGameOverlay(event);
        }

    }

}

