package Method.Client.clickgui;

import Method.Client.Main;
import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.Frame;
import Method.Client.clickgui.component.components.Button;
import Method.Client.clickgui.component.components.sub.Keybind;
import Method.Client.managers.FileManager;
import Method.Client.module.Category;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.module.command.Command;
import Method.Client.module.command.CommandManager;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static Method.Client.clickgui.component.Component.FontRend;
import static Method.Client.module.Onscreen.OnscreenGUI.pinableFrames;
import static Method.Client.module.command.CommandManager.commands;
import static org.lwjgl.input.Keyboard.KEY_TAB;

public class ClickGui extends GuiScreen {
    public static ArrayList<Frame> frames = new ArrayList<>();
    private GuiTextField textbox;
    boolean nomultidrag = false;
    boolean loaded;
    boolean Trycommand = false;

    public ClickGui() {
        int frameX = 5;

        for (Category category : Category.values()) {
            Frame frame = new Frame(category);
            frame.setX(frameX);
            frames.add(frame);
            frameX += frame.getWidth() + 1;
        }

        FileManager.LoadMods();

        loaded = true;
        for (PinableFrame me : pinableFrames) {
            me.setup();
        }

    }


    @Override
    public void initGui() {
        textbox = new GuiTextField(0, fontRenderer, (int) (mc.displayWidth / 5.4), 1, 240, mc.displayWidth / 100);
        textbox.setFocused(true);
        textbox.setMaxStringLength(999);
        textbox.setEnableBackgroundDrawing(false);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GlStateManager.scale(1f, 1f, 0.5f);
        String parse = textbox.getText();
        textbox.drawTextBox();
        Frame.updateFont();
        Button.updateFont();

        for (Frame frame : frames) {
            if (frame.isWithinBounds(mouseX, mouseY))
                frame.handleScrollinput();
            frame.updatePosition(mouseX, mouseY);
            frame.renderFrame();
            if (frame.isOpen())
                for (Component comp : frame.getComponents()) {
                    comp.RenderTooltip();
                    if (comp.getName().toLowerCase().contains(parse.toLowerCase()) && !parse.isEmpty())
                        RenderUtils.drawRectOutline(frame.getX(), comp.gety(), frame.getX() + 88, comp.gety() + 12, 1, ColorUtils.rainbow().getRGB());
                    try {
                        comp.updateComponent(mouseX, mouseY);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

        this.drawGradientRect((int) (mc.displayWidth / 5.4), 0, (int) ((mc.displayWidth / 5.4) + 240), 14, 0x33999999, 0x33999999);
        if (!parse.isEmpty()) {
            int add = 0;
            for (Command c : commands) {
                if (c.getCommand().toLowerCase().startsWith(parse.toLowerCase())) {
                    FontRend.drawStringWithShadow(c.getSyntax(), (float) (mc.displayWidth / 5.4), add + 10, -1);
                    add += 10;
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (nomultidrag) {
            Collections.reverse(frames);
            nomultidrag = false;
        }
        for (Frame frame : frames) {
            if (frame.isOpen()) {
                if (!frame.getComponents().isEmpty()) {
                    if (frame.isWithinBounds(mouseX, mouseY))
                        for (Component component : frame.getComponents()) {
                            component.mouseClicked(mouseX, mouseY, mouseButton);
                        }
                }
            }
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 0 && !nomultidrag) {
                frame.setDrag(true);
                frame.dragX = mouseX - frame.getX();
                frame.dragY = mouseY - frame.getY();
                nomultidrag = true;
            }
            if (frame.isWithinFooter(mouseX, mouseY) && mouseButton == 0) {
                frame.dragScrollstop = mouseY - frame.getScrollpos();
                frame.setDragBot(true);
            }
            if (frame.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
                if (frame.getName().equalsIgnoreCase("Onscreen"))
                    mc.displayGuiScreen(Main.OnscreenGUI);
                else
                    frame.setOpen(!frame.isOpen());
            }

        }


        if (mouseButton == 0 && !(mouseY < 14 && mouseX < mc.displayWidth / 5.4 + 240 && mouseX > mc.displayWidth / 5.4 - 5)) {
            textbox.setText("");
            Trycommand = false;
        }
        if (mouseY < 14 && mouseButton == 0 && mouseX < (mc.displayWidth / 5.4) + 240 && mouseX > (mc.displayWidth / 5.4) - 5) {
            Trycommand = true;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (loaded) {
            FileManager.SaveMods();
            FileManager.saveframes();
            FileManager.savePROFILES();
        }
        if (keyCode == 1)
            this.mc.displayGuiScreen(null);
        if (!Keybind.PublicBinding)
            textbox.textboxKeyTyped(typedChar, keyCode);

        for (Frame frame : frames) {
            if (frame.isOpen() && keyCode != 1) {
                if (!frame.getComponents().isEmpty()) {
                    for (Component component : frame.getComponents()) {
                        component.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }

        if (typedChar == KEY_TAB) {
            for (Command c : commands) {
                String parse = textbox.getText();
                if (parse.length() > 0)
                    if (c.getCommand().toLowerCase().startsWith(parse.toLowerCase()) || parse.toLowerCase().startsWith(c.getCommand())) {
                        textbox.setText(c.getCommand());
                        break;
                    }
            }
        }
        if (textbox.isFocused() && keyCode == 28 && Trycommand) {
            CommandManager.getInstance().runCommands(CommandManager.cmdPrefix + textbox.getText());
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void updateScreen() {
        textbox.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Frame frame : frames) {
            frame.setDrag(false);
            frame.setDragBot(false);
            if (frame.isOpen())
                if (!frame.getComponents().isEmpty())
                    for (Component component : frame.getComponents())
                        component.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
