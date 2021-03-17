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
        RenderSub(parent, offset, this.hovered);
        Button.fontSelectButton(binding ? "Press a key..." : ("Key: " + Keyboard.getKeyName(this.parent.mod.getKey())), (parent.parent.getX() + 7), (parent.parent.getY() + offset + 2), -1);
        GlStateManager.popMatrix();
        if (this.hovered)
            Button.fontSelectButton("Press End To Clear", 0, (float) (mc.displayHeight / 2.085), 0xA6a83a32);
    }


    static void RenderSub(Button parent, int offset, boolean hovered) {
        glEnable(GL_BLEND);
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, hovered ? GuiModule.Hover.getcolor() : GuiModule.innercolor.getcolor());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xA6111111);
        GL11.glPushMatrix();
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        this.hovered = isMouseOnButton(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
            this.binding = !this.binding;
            PublicBinding = !PublicBinding;
        }

    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (this.binding) {
            this.parent.mod.setKey(key);
            this.binding = false;
            PublicBinding = false;
            if (this.parent.mod.getKey() == Keyboard.KEY_END) {
                this.parent.mod.setKey(0);
            }
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
