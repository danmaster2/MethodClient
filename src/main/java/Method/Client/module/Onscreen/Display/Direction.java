package Method.Client.module.Onscreen.Display;

import com.mojang.realmsclient.gui.ChatFormatting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.managers.Setting;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Direction extends Module {
    public Direction() {
        super("Direction", Keyboard.KEY_NONE, Category.ONSCREEN, "Direction");
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
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));

        setmgr.add(Background = new Setting("Background", this, false));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth ) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 40, -20, (mc.displayHeight) + 40, true));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));

    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("DirectionSET", true);
    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("DirectionSET", false);

    }

    public static class DirectionRUN extends PinableFrame {

        public DirectionRUN() {
            super("DirectionSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
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
            final String direction = String.format("%s" + " " + ChatFormatting.GRAY + "%s", this.getFacing(), this.getTowards());
            if (Background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, direction), this.y + 20, BgColor.getcolor());
            fontSelect(Frame, direction, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());

            super.onRenderGameOverlay(event);
        }

         public String getFacing() {
            switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
                case 0:
                    return "South";
                case 1:
                    return "South West";
                case 2:
                    return "West";
                case 3:
                    return "North West";
                case 4:
                    return "North";
                case 5:
                    return "North East";
                case 6:
                    return "East";
                case 7:
                    return "South East";
            }
            return "Invalid";
        }

        private String getTowards() {
            switch (MathHelper.floor((double) (mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7) {
                case 0:
                    return "+Z";
                case 1:
                    return "-X +Z";
                case 2:
                    return "-X";
                case 3:
                    return "-X -Z";
                case 4:
                    return "-Z";
                case 5:
                    return "+X -Z";
                case 6:
                    return "+X";
                case 7:
                    return "+X +Z";
            }
            return "Invalid";
        }
    }

}

