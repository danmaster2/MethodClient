package Method.Client.module.Onscreen.Display;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Tps extends Module {
    public Tps() {
        super("Tps", Keyboard.KEY_NONE, Category.ONSCREEN, "Tps");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting background;
    static Setting xpos;
    static Setting ypos;
    static Setting Shadow;
    static Setting Frame;
    static Setting FontSize;
    private static final float[] tickRates = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate;

    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
        nextIndex = 0;
        timeLastTimeUpdate = -1L;
        Arrays.fill(tickRates, 0.0F);
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(background = new Setting("background", this, false));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth ) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 210, -20, (mc.displayHeight) + 40, true));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("TpsSET", true);

    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("TpsSET", false);

    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof SPacketTimeUpdate) {
            onTimeUpdate();
        }
        return true;
    }

    private void onTimeUpdate() {
        if (this.timeLastTimeUpdate != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0F;
            tickRates[(this.nextIndex % tickRates.length)] = MathHelper.clamp(20.0F / timeElapsed, 0.0F, 20.0F);
            this.nextIndex += 1;
        }
        this.timeLastTimeUpdate = System.currentTimeMillis();
    }

    public static float getTickRate() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : tickRates) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks += 1.0F;
            }
        }
        return MathHelper.clamp(sumTickRates / numTicks, 0.0F, 20.0F);
    }

    public static class TpsRUN extends PinableFrame {

        public TpsRUN() {
            super("TpsSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
        }

        @Override
        public void setup() {
            GetSetup(this, xpos, ypos, Frame, FontSize);
        }

        @Override
        public void Ongui() {
            GetInit(this, xpos, ypos, Frame, FontSize);

        }

        @Override
        public void onRenderGameOverlay(Text event) {

            final String tickrate = String.format("TPS: %.2f", getTickRate());

            fontSelect(Frame, tickrate, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());
            if (background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, tickrate), this.y + 20, BgColor.getcolor());
            super.onRenderGameOverlay(event);

        }


    }

}

