package Method.Client.module.Onscreen.Display;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Hunger extends Module {
    public Hunger() {
        super("Hunger", Keyboard.KEY_NONE, Category.ONSCREEN, "Hunger");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting xpos;
    static Setting ypos;
    static Setting Frame;
    static Setting Background;
    static Setting Shadow;
    static Setting FontSize;

    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(Background = new Setting("Background", this, false));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth ) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 100, -20, (mc.displayHeight) + 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("HungerSET", true);

    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("HungerSET", false);

    }

    public static class HungerRUN extends PinableFrame {

        public HungerRUN() {
            super("HungerSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
        }

        @Override
        public void setup() {
            GetSetup(this, xpos, ypos, Frame, FontSize);
        }

        @Override
        public void Ongui() {
            GetInit(this, xpos, ypos, Frame, FontSize);

        }

        public static Field foodExhaustionLevel;

        @Override
        public void onRenderGameOverlay(Text event) {

            final DecimalFormat dc = new DecimalFormat("#.##");

            String cow = "Hunger: " + mc.player.getFoodStats().getFoodLevel() + " Saturation: "
                    + dc.format(mc.player.getFoodStats().getSaturationLevel());

            fontSelect(Frame, cow, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());
            if (Background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, cow), this.y + 20, BgColor.getcolor());
            super.onRenderGameOverlay(event);

        }

    }

}

