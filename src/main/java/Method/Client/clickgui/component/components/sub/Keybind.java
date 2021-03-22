package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.module.misc.GuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;

public class Keybind extends Component {

    private boolean hovered;
    private boolean binding;
    public static boolean PublicBinding;
    private final Button parent;
    private int offset;
    private int x;
    private int y;

    private boolean LControl = false;
    private boolean LShift = false;
    private boolean LAlt = false;
    protected Minecraft mc = Minecraft.getMinecraft();

    public Keybind(Button button, int offset) {
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void renderComponent() {
        glEnable(GL_BLEND);
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? GuiModule.Hover.getcolor() : GuiModule.innercolor.getcolor());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xA6111111);
        GL11.glPushMatrix();
        StringBuilder Keys = new StringBuilder();
        for (Integer key : this.parent.mod.getKeys()) {
            Keys.append(" ").append(Keyboard.getKeyName(key));
        }
        Button.fontSelectButton((this.binding) ? "Press a key..." : ("Key: " + Keys),
                (parent.parent.getX() + 7), (parent.parent.getY() + offset + 2), -1);
        GlStateManager.popMatrix();
    }

    @Override
    public void RenderTooltip() {
        if (this.hovered && this.parent.open) {
            Button.fontSelect("Press End To Clear", 0, (float) (mc.displayHeight / 2.085), -1);
        }
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.y = this.parent.parent.getY() + this.offset;
        this.x = this.parent.parent.getX();
        this.hovered = isMouseOnButton(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.hovered && button == 0 && this.parent.open) {
            this.binding = !this.binding;
            PublicBinding = !PublicBinding;
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.binding) {
            if (key == Keyboard.KEY_LCONTROL) {
                LControl = !LControl;
                return;
            }
            if (key == Keyboard.KEY_LSHIFT) {
                LShift = !LShift;
                return;
            }
            if (key == Keyboard.KEY_LMENU) {
                LAlt = !LAlt;
                return;
            }
            this.parent.mod.setKey(key, LControl, LShift, LAlt);
            this.binding = false;
            this.LAlt = false;
            this.LControl = false;
            this.LShift = false;
            PublicBinding = false;
            if (key == Keyboard.KEY_END) {
                this.parent.mod.setKey(key, false, false, false);
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + this.parent.parent.getWidth() && y > this.y && y < this.y + this.parent.parent.getBarHeight();
    }
}
