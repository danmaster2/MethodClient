package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.managers.Setting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;


public class Checkbox extends Component {

    private boolean hovered;
    private final Setting op;
    private final Button parent;
    private int offset;
    private int x;
    private int y;

    public Checkbox(Setting option, Button button, int offset) {
        this.op = option;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Keybind.RenderSub(parent, offset, this.hovered);
        Button.fontSelectButton(this.op.getName(), (parent.parent.getX() + 10 + 4), (parent.parent.getY() + offset + 2), -1);
        GlStateManager.popMatrix();
        Gui.drawRect(parent.parent.getX() + 3 + 4, parent.parent.getY() + offset + 3, parent.parent.getX() + 9 + 4, parent.parent.getY() + offset + 9, 0xA6999999);
        if (this.op.getValBoolean())
            Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, 0xFFf5b727);
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
            this.op.setValBoolean(!op.getValBoolean());
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
