package Method.Client.clickgui.component.components.sub;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.managers.Setting;
import Method.Client.module.misc.GuiModule;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;

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
        glEnable(GL_BLEND);
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth()), parent.parent.getY() + offset + 12, this.hovered ? GuiModule.Hover.getcolor() : GuiModule.innercolor.getcolor());
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, 0xA6111111);
        GL11.glPushMatrix();
        Button.fontSelectButton(this.set.getName() + ": " + this.set.getValString(), (float) ((parent.parent.getX() + 7)), (float) ((parent.parent.getY() + offset + 3)), -1);
        GlStateManager.popMatrix();

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
            modeIndex = (modeIndex + 1 >= set.getOptions().size()) ? 0 : modeIndex + 1;
            set.setValString(set.getOptions().get(modeIndex));
        }
        if (this.hovered && button == 1 && this.parent.open) {
            modeIndex = (modeIndex - 1 < 0) ? set.getOptions().size() - 1 : modeIndex - 1;
            set.setValString(set.getOptions().get(modeIndex));
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + this.parent.parent.getWidth() && y > this.y && y < this.y + this.parent.parent.getBarHeight();
    }
}
