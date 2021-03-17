package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.managers.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static Method.Client.utils.system.Wrapper.mc;


public class Guibutton extends Component {

    private boolean hovered;
    private final Setting op;
    private final Button parent;
    private int offset;
    private int x;
    private int y;
    private GuiScreen screen;

    public Guibutton(Setting option, Button button, int offset,GuiScreen screen) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
        this.screen = screen;
    }

    @Override
    public void renderComponent() {
        Keybind.RenderSub(parent, offset, this.hovered);
        Button.fontSelectButton(this.op.getName(), (parent.parent.getX() + 35), (parent.parent.getY() + offset + 2), -1);
        GlStateManager.popMatrix();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public String getName() {
        return this.op.getName();
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
            mc.displayGuiScreen(this.screen);
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
