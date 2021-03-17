package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.managers.Setting;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ModeButton extends Component {

    private boolean hovered;
    private final Button parent;
    private final Setting set;
    private int offset;
    private int x;
    private int y;

    private int modeIndex;

    public ModeButton(Setting set, Button button, int offset) {
        this.set = set;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
        this.modeIndex = 0;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void renderComponent() {
        Keybind.RenderSub(parent, offset, this.hovered);
        Button.fontSelectButton(this.set.getName() + ": " + this.set.getValString(), (float) ((parent.parent.getX() + 7)), (float) ((parent.parent.getY() + offset + 3)), -1);
        GlStateManager.popMatrix();

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
            modeIndex = modeIndex + 1 >= set.getOptions().size() ? 0 : modeIndex + 1;
            set.setValString(set.getOptions().get(modeIndex));
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12;
    }
}
