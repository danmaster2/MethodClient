package Method.Client.clickgui.component;

import Method.Client.Main;
import Method.Client.clickgui.ClickGui;
import Method.Client.clickgui.component.components.Button;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.misc.GuiModule;
import Method.Client.module.misc.ModSettings;
import Method.Client.utils.font.CFont;
import Method.Client.utils.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;


public class Frame {

    public ArrayList<Component> components;
    public Category category;
    private boolean open;
    private int width;
    private int y;
    private int x;
    private int Bottomy;
    private int scrollpos;
    private int CollapseRate;
    private final int barHeight;
    private boolean isDragging;
    private boolean isDraggingBot;
    public int dragX;
    public int dragScrollstop;
    public int dragY;
    protected Minecraft mc = Minecraft.getMinecraft();
    int scroll;
    boolean wasopen;
    public static CFontRenderer FrameFont;

    public Frame(Category cat) {
        this.components = new ArrayList<>();
        this.category = cat;
        this.width = 88;
        this.x = 5;
        this.y = 20;
        this.barHeight = 12;
        this.CollapseRate = 0;
        this.dragX = 0;
        this.open = false;
        this.isDragging = false;
        this.isDraggingBot = false;
        this.wasopen = false;

        int tY = this.barHeight;
        for (Module mod : ModuleManager.getModulesInCategory(category)) {
            Button modButton = new Button(mod, this, tY);
            this.components.add(modButton);
            tY += this.barHeight;
        }
        this.Bottomy = tY;
    }

    public static void updateFont() {
        if (GuiModule.Frame.getValString().equalsIgnoreCase("Arial"))
            FrameFont = CFont.afontRenderer26;
        if (GuiModule.Frame.getValString().equalsIgnoreCase("Times"))
            FrameFont = CFont.tfontRenderer26;
        if (GuiModule.Frame.getValString().equalsIgnoreCase("Impact"))
            FrameFont = CFont.ifontRenderer26;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void updateRefresh() {
        int tY = this.barHeight;
        this.components.clear();
        for (Module mod : ModuleManager.getModulesInCategory(category)) {
            Button modButton = new Button(mod, this, tY);
            this.components.add(modButton);
            tY += this.barHeight;
        }
        this.Bottomy = tY;
    }


    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setBottomy(int setby) {
        this.Bottomy = setby;
    }

    public void setScrollpos(int NewY) {
        this.scrollpos = NewY;
    }

    public void setDrag(boolean drag) {
        this.isDragging = drag;
    }

    public void setDragBot(boolean drag) {
        this.isDraggingBot = drag;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        if (open) {
            this.wasopen = true;
        }
    }

    public void renderFrame() {
        if (mc.currentScreen instanceof ClickGui && this.category.name().equalsIgnoreCase("Onscreen")) {
            this.setX((int) ((mc.displayWidth / 5.4) - this.width));
            this.setY(2);
            this.setWidth(22);
        }
        glEnable(GL_BLEND);
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.barHeight, GuiModule.Framecolor.getcolor());
        if (this.open && !this.category.name().equalsIgnoreCase("Profiles") && this.CollapseRate < 5)
            Gui.drawRect(this.x, this.Bottomy + this.scrollpos + this.y, this.x + this.width, this.Bottomy + this.barHeight + this.scrollpos + this.y, GuiModule.Framecolor.getcolor());
        GlStateManager.pushMatrix();
        if (!this.category.name().equalsIgnoreCase("Onscreen")) {
            fontSelect(this.category.name(), this.x + 3, (float) (this.y + 2.5f - 1.5), -1);
            fontSelect(this.open ? "-" : "+", this.x + this.width - 9, (float) ((this.y + 2.5f) - 1.5), -1);
        }
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(new ResourceLocation(Main.MODID, this.getName().toLowerCase() + ".png"));
        Gui.drawModalRectWithCustomSizedTexture(this.x + this.width - this.barHeight - 6, this.y + 1, 0, 0, this.barHeight, this.barHeight, this.barHeight, this.barHeight);
        if ((this.open || (this.wasopen && GuiModule.Animations.getValBoolean())) && !this.components.isEmpty()) {
            if (!this.open) {
                if (this.CollapseRate + this.barHeight + this.barHeight < (this.Bottomy + this.scrollpos - this.barHeight) * 2)
                    this.CollapseRate += ModSettings.GuiSpeed.getValDouble();
                else {
                    this.wasopen = false;
                    return;
                }
            } else {
                if (this.CollapseRate > 0)
                    this.CollapseRate -= ModSettings.GuiSpeed.getValDouble();
                else
                    this.CollapseRate = 0;
            }
            if (this.category.name().equalsIgnoreCase("Profiles"))
                this.components.forEach(Component::renderComponent);
            else {
                glEnable(GL_SCISSOR_TEST);
                GL11.glScissor(this.x * 2, (mc.displayHeight - ((this.Bottomy + this.y + this.scrollpos) * 2)) + this.CollapseRate, this.width * 2, ((this.Bottomy + this.scrollpos - this.barHeight) * 2) - this.CollapseRate);
                this.components.forEach(Component::renderComponent);
                glDisable(GL_SCISSOR_TEST);
            }
        }
    }

    public void handleScrollinput() {
        int off = this.barHeight;
        for (Component component : this.components) {
            component.setOff(off - (this.barHeight * this.scroll));
            off += component.getHeight();
        }
        boolean Canscoll = false;
        off = off - ((  this.barHeight) * this.scroll) ;
        if (off > this.Bottomy + this.scrollpos)
            Canscoll = true;

        int wheel = Mouse.getDWheel();
        if (wheel < 0 && Canscoll)
            this.scroll++;
        else if (wheel > 0)
            this.scroll--;
        if (this.scroll < 0)
            this.scroll = 0;
    }

    public void fontSelect(String name, float v, float v1, int i) {
        if (GuiModule.Frame.getValString().equalsIgnoreCase("MC"))
            mc.fontRenderer.drawStringWithShadow(name, (int) v, (int) v1, i);
        else
            FrameFont.drawStringWithShadow(name, v, v1, i);
    }

    public void refresh() {
        int off = this.barHeight;
        for (Component comp : this.components) {
            comp.setOff(off);
            off += comp.getHeight();
        }
    }


    public int getX() {
        return x;
    }

    public String getName() {
        return this.category.name();
    }

    public int getY() {
        return y;
    }

    public int getScrollpos() {
        return scrollpos;
    }

    public int getWidth() {
        return width;
    }

    public int getBarHeight() {
        return barHeight;
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (this.isDragging) {
            if (!((mouseX - dragX + this.width > mc.displayWidth / 2) || ((mouseX - dragX)) < 0))
                this.setX(mouseX - dragX);

            if (!(mouseY - dragY + this.barHeight > mc.displayHeight / 2))
                this.setY(mouseY - dragY);
        }
        int off = this.barHeight;
        for (Component component : this.components) {
            off += component.getHeight();
        }

        if (this.isDraggingBot) {
            if (!(mouseY + 6 > off + this.y + this.barHeight))
                this.setScrollpos(mouseY - this.dragScrollstop);
            handleScrollinput();
        }

        // Bottomy is the static value from top bar to bottom bar not expanded

        if (this.scrollpos + this.Bottomy > off) {
            this.setScrollpos(this.scrollpos - this.barHeight);
        }
        if ((this.scrollpos < 0 && this.scrollpos > -12) || (this.scrollpos > 0 && this.scrollpos < 12)) {
            this.scrollpos = 0;
        }

        off = off - (this.barHeight * this.scroll);
        if (!(off > this.Bottomy + this.scrollpos) && this.scroll > 0) {
            this.scroll--;
        }

        if (this.scrollpos < -this.Bottomy + this.barHeight)
            this.scrollpos = -this.Bottomy + this.barHeight;

    }

    public boolean isWithinHeader(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight;
    }

    public boolean isWithinFooter(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.Bottomy + this.scrollpos + this.y && y <= this.Bottomy + this.scrollpos + this.barHeight + this.y;
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.Bottomy + (!(this.category == Category.PROFILES) ? this.scrollpos : 1000);
    }

}
