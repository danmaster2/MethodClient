package Method.Client.module.Onscreen.Display;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Enemypos extends Module {
    public Enemypos() {
        super("Enemypos", Keyboard.KEY_NONE, Category.ONSCREEN, "Enemypos");
    }

    static Setting TextColor;
    static Setting BgColor;
    static Setting Friends;
    static Setting xpos;
    static Setting ypos;
    static Setting Frame;
    static Setting ColorDistance;
    static Setting Background;
    static Setting LefttoRight;
    static Setting Shadow;
    static Setting FontSize;

    @Override
    public void setup() {
        this.visible = false;
        setmgr.add(TextColor = new Setting("TextColor", this, 0, 1, 1, 1));
        setmgr.add(BgColor = new Setting("BGColor", this, .01, 0, .30, .22));
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(LefttoRight = new Setting("LefttoRight", this, true));
        setmgr.add(Friends = new Setting("Friends", this, true));
        setmgr.add(ColorDistance = new Setting("ColorDistance", this, true));
        setmgr.add(Background = new Setting("Background", this, false));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth/2) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 60, -20, (mc.displayHeight/2)  + 40, true));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("EnemyposSET", true);

    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("EnemyposSET", false);

    }

    public static class EnemyposRUN extends PinableFrame {

        public EnemyposRUN() {
            super("EnemyposSET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
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
            int yCount = this.y + this.barHeight + 3;
            for (EntityPlayer player : mc.world.playerEntities) {
                if ((FriendManager.isFriend(player.getName()) && Friends.getValBoolean()) || player.getName().equals(mc.player.getName()))
                    continue;
                int Lr = LefttoRight.getValBoolean() ? widthcal(Frame, (player.getName() + Pos(player))) - 70 : -3;
                if (Background.getValBoolean())
                    drawRect(this.x + 4, yCount, widthcal(Frame, player.getName() + Pos(player)) + this.x + 3, yCount + heightcal(Frame, player.getName() + player.getPosition()) - 1,
                            Background.getcolor());
                fontSelect(Frame, player.getName() + Pos(player), this.x - Lr, yCount, ColorDistance.getValBoolean() ? distancecolor(player) : TextColor.getcolor(), Shadow.getValBoolean());
                yCount += 8;
            }

            super.onRenderGameOverlay(event);
        }

        private int distancecolor(EntityPlayer player) {
            int g = 0, r = 0;

            if (mc.player.getDistance(player) > 50 && mc.player.getDistance(player) < 100) {
                g = (int) ((mc.player.getDistance(player) - 50) * 5.1);
            }
            if (mc.player.getDistance(player) < 50) {
                r = (int) ((mc.player.getDistance(player)) * 5.1);
            }

            mc.player.getDistance(player);
            return new Color(r, g, 0).getRGB();
        }

        public String Pos(EntityPlayer player) {
            return " X:" + player.getPosition().getX() + ", Y:" + player.getPosition().getY() + ", Z:" + player.getPosition().getZ();
        }
    }

}

