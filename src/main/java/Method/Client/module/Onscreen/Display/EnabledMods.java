package Method.Client.module.Onscreen.Display;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.utils.visual.ColorUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class EnabledMods extends Module {
    public EnabledMods() {
        super("EnabledMods", Keyboard.KEY_NONE, Category.ONSCREEN, "EnabledMods");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting background;
    static Setting xpos;
    static Setting ypos;
    static Setting ToptoBottom;
    static Setting LefttoRight;
    static Setting Frame;
    static Setting Shadow;
    static Setting FontSize;
    static Setting Wave;


    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(TextColor = new Setting("TextColor", this, 0, .75, .85, .85));
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(Wave = new Setting("Wave", this, true));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(ToptoBottom = new Setting("ToptoBottom", this, true));
        setmgr.add(LefttoRight = new Setting("LefttoRight", this, false));
        setmgr.add(background = new Setting("background", this, false));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
        setmgr.add(xpos = new Setting("xpos", this, 0, -20, (mc.displayWidth) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 0, -20, (mc.displayHeight) + 40, true));
        this.setToggled(true);
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("EnabledModsSet", true);
    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("EnabledModsSet", false);
    }

    public static class EnabledModsRUN extends PinableFrame {

        public EnabledModsRUN() {
            super("EnabledModsSet", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
        }

        @Override
        public void setup() {
            GetSetup(this, xpos, ypos, Frame, FontSize);
            setPinned(true);
        }

        @Override
        public void Ongui() {
            GetInit(this, xpos, ypos, Frame, FontSize);
        }

        @Override
        public void onRenderGameOverlay(Text event) {
            int yCount = this.y + 3;
            for (Module module : ModuleManager.getSortedMods(ToptoBottom.getValBoolean(), true, true)) {
                int Lr = LefttoRight.getValBoolean() ? widthcal(Frame, (module.getDisplayName())) - 70 : -3;
                if (background.getValBoolean())
                    drawRect(this.x - Lr, yCount + 3, widthcal(Frame, module.getDisplayName()) + this.x + 2 - Lr, yCount + heightcal(Frame, module.getDisplayName()) + 1, BgColor.getcolor());
                fontSelect(Frame, module.getDisplayName(), this.x - Lr, yCount, Wave.getValBoolean() ? ColorUtils.wave((yCount - this.y - this.barHeight - 3) / 8, TextColor.getSat(), TextColor.getBri()).getRGB() : TextColor.getcolor(), Shadow.getValBoolean());
                yCount += 9;
            }
            super.onRenderGameOverlay(event);
        }
    }
}