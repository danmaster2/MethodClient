package Method.Client.module.Onscreen.Display;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static net.minecraft.client.gui.Gui.drawRect;

public final class Durability extends Module {
    public Durability() {
        super("Durability", Keyboard.KEY_NONE, Category.ONSCREEN, "Durability");
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
        setmgr.add(Shadow = new Setting("Shadow", this, true));
        setmgr.add(Background = new Setting("Background", this, false));
        setmgr.add(xpos = new Setting("xpos", this, 200, -20, (mc.displayWidth ) + 40, true));
        setmgr.add(ypos = new Setting("ypos", this, 20, -20, (mc.displayHeight) + 40, true));
        setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
        setmgr.add(FontSize = new Setting("FontSize", this, 22, 10, 40, true));
    }

    @Override
    public void onEnable() {
        PinableFrame.Toggle("DurabilitySET", true);
    }

    @Override
    public void onDisable() {
        PinableFrame.Toggle("DurabilitySET", false);

    }

    public static class DurabilityRUN extends PinableFrame {

        public DurabilityRUN() {
            super("DurabilitySET", new String[]{}, (int) ypos.getValDouble(), (int) xpos.getValDouble());
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

            ItemStack stack = mc.player.getHeldItemMainhand();

            if (!stack.isEmpty() && (stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword)) {
                final String Durability = "Durability: " + (stack.getMaxDamage() - stack.getItemDamage());


                if (Background.getValBoolean())
                    drawRect(this.x, this.y + 10, this.x + widthcal(Frame, Durability), this.y + 22, BgColor.getcolor());
                fontSelect(Frame, Durability, this.getX(), this.getY() + 10, TextColor.getcolor(), Shadow.getValBoolean());
            }
            super.onRenderGameOverlay(event);

        }
    }

}

