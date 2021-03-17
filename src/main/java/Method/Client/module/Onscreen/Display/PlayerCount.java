package Method.Client.module.Onscreen.Display;

import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class PlayerCount extends Module {
    public PlayerCount() {
        super("PlayerCount", Keyboard.KEY_NONE, Category.ONSCREEN, "PlayerCount");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting Friends;
    static Setting background;
    static Setting Shadow;
    static Setting xpos;
    static Setting ypos;
    static Setting Frame;
    static Setting FontSize;

    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(Friends = new Setting("Friends", this, false));
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(background = new Setting("background", this, false));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth ) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 140, -20, (mc.displayHeight) + 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("PlayerCountSET", true);

    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("PlayerCountSET", false);

    }

    public static class PlayerCountRUN extends PinableFrame {

        public PlayerCountRUN() {
            super("PlayerCountSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
        }

        @Override
        public void setup() {
            GetSetup(this, xpos, ypos, Frame, FontSize);
        }

        @Override
        public void Ongui() {
            GetInit(this, xpos, ypos, Frame, FontSize);
        }

        int lasting = 0;

        @Override
        public void onRenderGameOverlay(Text event) {
            String playerCount = "ONLINE: " + mc.player.connection.getPlayerInfoMap().size();
            if (Friends.getValBoolean()) {

                if (mc.player.ticksExisted % 20 == 0) {
                    int onlinefriend = 0;
                    for (EntityPlayer s : mc.world.playerEntities) {
                        if (FriendManager.friendsList.contains(s.getName()))
                            onlinefriend++;
                    }
                    lasting = onlinefriend;
                }
                playerCount += " Friends: " + lasting;
            }
            fontSelect(Frame, playerCount, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());
            if (background.getValBoolean())
                drawRect(this.x, this.y + 10, this.x + widthcal(Frame, playerCount), this.y + 20, BgColor.getcolor());

            super.onRenderGameOverlay(event);

        }
    }

}

