package Method.Client.module.Onscreen.Display;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Angles extends Module {
    public Angles() {
        super("Angles", Keyboard.KEY_NONE, Category.ONSCREEN, "Angles");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting background;
    static Setting xpos;
    static Setting ypos;
    static Setting Frame;
    static Setting Decimal;
    static Setting Shadow;
    static Setting FontSize;
    static Setting TrueYAW;
    static Setting Names;

    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(TrueYAW = new Setting("TrueYAW", this, false));
        setmgr.add(Names = new Setting("Names", this, true));
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(BgColor = new Setting("BgColor", this, .22, .88, .22, .22));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(background = new Setting("background", this, false));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth ) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 220, -20, (mc.displayHeight) + 40, true));
        setmgr.add(Decimal = new Setting("Decimal", this, 2, 0, 5, true));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("AnglesSET", true);
    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("AnglesSET", false);
    }

    public static class AnglesRUN extends PinableFrame {

        public AnglesRUN() {
            super("AnglesSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

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
            if (background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, getCoords()), this.y + 20, background.getcolor());
            fontSelect(Frame, getCoords(), this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());
            if (background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, getCoords()), this.y + 22, BgColor.getcolor());
            super.onRenderGameOverlay(event);
        }

        private String getCoords() {
            decimalFormat = getDecimalFormat((int) Decimal.getValDouble());

            String Pitch = decimalFormat.format(mc.player.rotationPitch);
            String Yaw;
            if (!TrueYAW.getValBoolean())
                Yaw = decimalFormat.format(mc.player.rotationYaw % 360);
            else
                Yaw = decimalFormat.format(mc.player.rotationYaw);
            if (Names.getValBoolean())
                return "Pitch " + Pitch + ", Yaw " + Yaw;
            return Pitch + ", " + Yaw;
        }
    }

}

