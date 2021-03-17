package Method.Client.utils.Screens.Custom.Xray;

import Method.Client.Main;
import Method.Client.managers.FileManager;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.render.Xray;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class XrayGui extends GuiScreen {

    private ListGui listGui;
    private ListGui Allblocks;
    private GuiTextField blockNameField;
    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiButton doneButton;
    private Block blockToAdd;


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {

        listGui = new ListGui(mc, this, XrayGuiSettings.getBlockNames(), 500, 0);

        Allblocks = new ListGui(mc, this, XrayGuiSettings.getAllBlockNames(XrayGuiSettings.getBlockNames()), 0, 0);

        blockNameField = new GuiTextField(1, mc.fontRenderer, 64, height - 55, 150, 18);

        buttonList.add(addButton = new GuiButton(0, 214, height - 56, 30, 20, "Add"));

        buttonList.add(removeButton = new GuiButton(1, width - 300, height - 56, 100, 20, "Remove Selected"));

        buttonList.add(new GuiButton(2, width - 108, 8, 100, 20, "Reset to Defaults"));

        buttonList.add(doneButton = new GuiButton(3, width / 2 - 100, height - 28, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch (button.id) {
            case 0:
                XrayGuiSettings.add(blockToAdd);
                blockNameField.setText("");
                Allblocks.list = XrayGuiSettings.getAllBlockNames(XrayGuiSettings.getBlockNames());
                break;

            case 1:
                XrayGuiSettings.remove(listGui.selected);
                break;

            case 2:
                mc.displayGuiScreen(new GuiYesNo(this, "Reset to Defaults", "Are you sure?", 0));
                break;

            case 3:
                mc.displayGuiScreen(Main.ClickGui);
                Module xray = ModuleManager.getModuleByName("Xray");
                if (xray.isToggled()) {
                    Xray.blockNames = new ArrayList<>(XrayGuiSettings.getBlockNames());
                    mc.renderGlobal.loadRenderers();
                }
                FileManager.saveXRayData();
                break;
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (id == 0 && result)
            XrayGuiSettings.resetToDefaults();

        super.confirmClicked(result, id);
        mc.displayGuiScreen(this);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        listGui.handleMouseInput(mouseX, mouseY);
        Allblocks.handleMouseInput(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
            throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        blockNameField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseX < 550 || mouseX > width - 50 || mouseY < 32 || mouseY > height - 64) {
            listGui.selected = -1;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        blockNameField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN)
            actionPerformed(addButton);
        else if (keyCode == Keyboard.KEY_DELETE)
            actionPerformed(removeButton);
        else if (keyCode == Keyboard.KEY_ESCAPE)
            actionPerformed(doneButton);
        if (!blockNameField.getText().isEmpty())
            Allblocks.list = XrayGuiSettings.SearchBlocksAll(XrayGuiSettings.getBlockNames(), blockNameField.getText());
        else
            Allblocks.list = XrayGuiSettings.getAllBlockNames(XrayGuiSettings.getBlockNames());
    }

    @Override
    public void updateScreen() {
        blockNameField.updateCursorCounter();

        blockToAdd = Block.getBlockFromName(blockNameField.getText());
        if (blockNameField.getText().isEmpty() && Allblocks.selected >= 0 && Allblocks.selected < Allblocks.list.size())
            blockToAdd = Block.getBlockFromName(Allblocks.list.get(Allblocks.selected));
        addButton.enabled = blockToAdd != null;

        removeButton.enabled = listGui.selected >= 0 && listGui.selected < listGui.list.size();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(mc.fontRenderer, "XRAY" + " (" + listGui.getSize() + ")", width / 2, 12, 0xffffff);

        listGui.drawScreen(mouseX, mouseY, partialTicks);

        Allblocks.drawScreen(mouseX, mouseY, partialTicks);

        blockNameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (blockNameField.getText().isEmpty() && !blockNameField.isFocused())
            drawString(mc.fontRenderer, "block name or ID", 68,
                    height - 50, 0x808080);

        drawRect(48, height - 56, 64, height - 36, 0xffa0a0a0);
        drawRect(49, height - 55, 64, height - 37, 0xff000000);
        drawRect(214, height - 56, 244, height - 55, 0xffa0a0a0);
        drawRect(214, height - 37, 244, height - 36, 0xffa0a0a0);
        drawRect(244, height - 56, 246, height - 36, 0xffa0a0a0);
        drawRect(214, height - 55, 243, height - 52, 0xff000000);
        drawRect(214, height - 40, 243, height - 37, 0xff000000);
        drawRect(215, height - 55, 216, height - 37, 0xff000000);
        drawRect(242, height - 55, 245, height - 37, 0xff000000);

        listGui.renderIconAndGetName(new ItemStack(blockToAdd), height - 52);
        Allblocks.renderIconAndGetName(new ItemStack(blockToAdd), height - 52);
    }

    private static class ListGui extends GuiScrollingList {
        private final Minecraft mc;
        private List<String> list;
        private int selected = -1;
        private int offsetx;

        public ListGui(Minecraft mc, XrayGui screen, List<String> list, int offsetx, int offsety) {
            super(mc, screen.width / 4, screen.height, 32 + offsety, screen.height - 64, 50 + offsetx, 16, screen.width, screen.height);
            this.offsetx = offsetx;
            this.mc = mc;
            this.list = list;
        }

        @Override
        protected int getSize() {
            return list.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick) {
            if (index >= 0 && index < list.size())
                selected = index;
        }

        @Override
        protected boolean isSelected(int index) {
            return index == selected;
        }

        @Override
        protected void drawBackground() {
            drawRect(50 + offsetx, top, 66 + offsetx, bottom, 0xffffffff);
        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
            String name = list.get(slotIdx);

            ItemStack stack = new ItemStack(Objects.requireNonNull(Block.getBlockFromName(name)));
            FontRenderer fr = mc.fontRenderer;

            String displayName = renderIconAndGetName(stack, slotTop);
            if (displayName.equalsIgnoreCase("Air") && name.contains(":") && !name.endsWith(":"))
                displayName = name.substring(name.lastIndexOf(":"));
            fr.drawString(displayName + " (" + name + ")", 68 + offsetx, slotTop + 2, 0xf0f0f0);
        }

        private String renderIconAndGetName(ItemStack stack, int y) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(52 + offsetx, y, 0);
            GlStateManager.scale(0.75, 0.75, 0.75);

            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
            RenderHelper.disableStandardItemLighting();

            GlStateManager.popMatrix();
            return stack.getDisplayName();
        }
    }
}
