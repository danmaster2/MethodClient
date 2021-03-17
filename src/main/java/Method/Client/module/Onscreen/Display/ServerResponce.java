package Method.Client.module.Onscreen.Display;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.managers.Setting;
import Method.Client.utils.system.Connection;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class ServerResponce extends Module {
    public ServerResponce() {
        super("ServerResponce", Keyboard.KEY_NONE, Category.ONSCREEN, "ServerResponce");
    }

    static Setting Delay;
    static Setting TextColor;
    static Setting BgColor;
    static Setting xpos;
    static Setting ypos;
    static Setting Shadow;
    static Setting Frame;
    static Setting FontSize;
    static Setting Background;

    private static long serverLastUpdated = 0;

    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(Delay = new Setting("Delay", this, 1, 1, 10, true));
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(Background = new Setting("Background", this, false));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth/2) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 180, -20, (mc.displayHeight/2)  + 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("ServerResponceSET", true);

    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.IN) {
            serverLastUpdated = System.currentTimeMillis();
        }
        return true;
    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("ServerResponceSET", false);

    }

    public static class ServerResponceRUN extends PinableFrame {

        public ServerResponceRUN() {
            super("ServerResponceSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
        }

        @Override
        public void setup() {
            GetSetup(this,xpos,ypos,Frame,FontSize);
        }

        @Override
        public void Ongui() {
            GetInit(this, xpos, ypos, Frame,FontSize);

        }

        String text = "Server Not Responding! ";

        @Override
        public void onRenderGameOverlay(Text event) {
            if (mc.isSingleplayer()) return;
            if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) return;
            if (!(Delay.getValDouble() * 1000L <= System.currentTimeMillis() - serverLastUpdated)) return;
            if (shouldPing()) {
                if (isDown("1.1.1.1", 80, 1111)) {
                    text = "Your internet is offline! ";
                } else {
                    text = "Server Not Responding! ";
                }
            }
            text = text.replaceAll("! .*", "! " + timeDifference() + "s");

            if (Background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, text), this.y + 20, BgColor.getcolor());
            fontSelect(Frame, text, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());

            super.onRenderGameOverlay(event);

        }

        private double timeDifference() {
            return (System.currentTimeMillis() - serverLastUpdated) / 1000d;
        }

        public static boolean isDown(String host, int port, int timeout) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeout);
                return false;
            } catch (IOException e) {
                return true; // Either timeout or unreachable or failed DNS lookup.
            }
        }

        private static long startTime = 0;

        private boolean shouldPing() {
            if (startTime == 0) startTime = System.currentTimeMillis();
            if (startTime + 1000 <= System.currentTimeMillis()) { // 1 second
                startTime = System.currentTimeMillis();
                return true;
            }
            return false;
        }

    }

}

