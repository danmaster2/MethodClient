package Method.Client.clickgui.component.components.sub;


import Method.Client.clickgui.ClickGui;
import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.components.Button;
import Method.Client.managers.Setting;
import Method.Client.module.Onscreen.OnscreenGUI;
import Method.Client.module.misc.GuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.lwjgl.input.Keyboard.KEY_LCONTROL;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glEnable;


public class Slider extends Component {
    protected Minecraft mc = Minecraft.getMinecraft();
    private boolean hovered;
    private final Setting set;
    private final Button parent;
    private int offset;
    private int x;
    private int y;
    private boolean dragging = false;
    private GuiTextField Inputbox;

    public double renderWidth;

    public Slider(Setting value, Button button, int offset) {
        this.set = value;
        this.parent = button;
        this.x = button.parent.getX() + button.parent.getWidth();
        this.y = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        glEnable(GL_BLEND);
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, this.hovered ? GuiModule.Hover.getcolor() : GuiModule.innercolor.getcolor());
        Gui.drawRect(parent.parent.getX() + 2, parent.parent.getY() + offset, parent.parent.getX() + (int) renderWidth, parent.parent.getY() + offset + 12, hovered ? 0xBF555555 : 0xBF444444);
        Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + 2, parent.parent.getY() + offset + 12, (set.getValDouble() > set.getMax() || set.getValDouble() < set.getMin()) ?  0xA6911111: 0xA6919191);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1f, 1f, 0.5f);
        Button.fontSelectButton(this.set.getName() + ": " + this.set.getValDouble(), (float) ((parent.parent.getX() + 8)), (float) ((parent.parent.getY() + offset + 2)), -1);
        GlStateManager.popMatrix();
        if (this.hovered)
            Button.fontSelectButton(this.set.getTooltip(), 0, (float) (mc.displayHeight / 2.085), this.hovered ? 0xA6999999 : -1);
        if (Inputbox != null)
            Inputbox.drawTextBox();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public String getName() {
        return this.set.getName();
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {
        if (Inputbox != null) Inputbox.updateCursorCounter();

        this.hovered = isMouseOnButtonD(mouseX, mouseY) || isMouseOnButtonI(mouseX, mouseY);
        this.y = parent.parent.getY() + offset;
        this.x = parent.parent.getX();

        double diff = Math.min(88, Math.max(0, mouseX - this.x));

        double min = set.getMin();
        double max = set.getMax();

        renderWidth = (88) * (set.getValDouble() - min) / (max - min);
        if (set.getValDouble() > max || set.getValDouble() < min) {
            renderWidth = .1;
        }
        if (dragging && (mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof OnscreenGUI)) {
            if (diff == 0) {
                set.setValDouble(set.getMin());
            } else {
                double newValue = roundToPlace(((diff / 88) * (max - min) + min));
                set.setValDouble(newValue);
            }
        }
        if (dragging && !(mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof OnscreenGUI))
            dragging = false;
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (Inputbox != null) {
            Inputbox.setFocused(false);
        }
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open && Inputbox == null) {
            dragging = true;
        }
        if (isMouseOnButtonD(mouseX, mouseY) && button == 0 && this.parent.open && Inputbox != null) {
            Inputbox.setFocused(true);
        }
        if (isMouseOnButtonI(mouseX, mouseY) && button == 0 && this.parent.open && Inputbox == null) {
            dragging = true;
        }
        if (this.hovered && Keyboard.isKeyDown(KEY_LCONTROL) && Inputbox == null) {
            Input();
            dragging = false;
        }
    }

    void Input() {
        Inputbox = new GuiTextField(0, FontRend, x + 5, y, 64, 10);
        Inputbox.setFocused(true);
        Inputbox.writeText(String.valueOf(set.getValDouble()));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (Inputbox != null) {
            Inputbox.textboxKeyTyped(typedChar, keyCode);
            if (Keyboard.getEventKey() == 28) {
                try {
                    set.setValDouble(Double.parseDouble(Inputbox.getText()));
                    Inputbox = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        dragging = false;
    }

    public boolean isMouseOnButtonD(int x, int y) {
        return x > this.x && x < this.x + (parent.parent.getWidth() / 2 + 1) && y > this.y && y < this.y + 12;
    }

    public boolean isMouseOnButtonI(int x, int y) {
        return x > this.x + parent.parent.getWidth() / 2 && x < this.x + parent.parent.getWidth() && y > this.y && y < this.y + 12;
    }

    private static double roundToPlace(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
