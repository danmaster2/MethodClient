package Method.Client.utils.Screens.Custom.Esp;

import Method.Client.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class EspGui extends GuiScreen {

    private static ListGui listGui;
    private ListGui AllMobs;
    private GuiTextField MobFieldName;
    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiButton doneButton;
    private String MobToAdd;
    private String MobToRemove;

    public static ArrayList<String> mobs = new ArrayList<>();
    public static ArrayList<String> allmobs = new ArrayList<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static List<String> RemoveMobs(List<String> input) {
        allmobs.clear();
        for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
            if (!input.contains(resourceLocation.toString()))
                allmobs.add(resourceLocation.toString());
        }
        return allmobs;
    }


    public static List<String> MobSearch(String text) {
        ArrayList<String> Temp = new ArrayList<>();
        for (ResourceLocation object : EntityList.getEntityNameList()) {
            String s = object.toString();
            if (object.toString().contains(text))
                Temp.add(s);
        }
        return Temp;
    }

    @Override
    public void initGui() {
        listGui = new ListGui(mc, this, mobs, width -  (width / 3), 0);

        AllMobs = new ListGui(mc, this, RemoveMobs(mobs), 0, 0);

        MobFieldName = new GuiTextField(1, mc.fontRenderer, 64, height - 55, 150, 18);

        buttonList.add(addButton = new GuiButton(0, 214, height - 56, 30, 20, "Add"));

        buttonList.add(removeButton = new GuiButton(1, width - 300, height - 56, 100, 20, "Remove Selected"));

        buttonList.add(new GuiButton(2, width - 108, 8, 100, 20, "Remove All"));

        buttonList.add(new GuiButton(20, width - 308, 8, 100, 20, "Add Passive"));

        buttonList.add(new GuiButton(40, width - 208, 8, 100, 20, "Add Hostile"));

        buttonList.add(doneButton = new GuiButton(3, width / 2 - 100, height - 28, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch (button.id) {
            case 0:
                if ((AllMobs.selected >= 0 && AllMobs.selected <= AllMobs.list.size())) {
                    if (!MobToAdd.isEmpty()) {
                        mobs.add(MobToAdd);
                        allmobs.remove(MobToAdd);
                        MobToAdd = "";
                    }
                }
                break;
            case 1:
                mobs.remove(listGui.selected);
                allmobs.add(MobToRemove);
                break;
            case 2:
                mc.displayGuiScreen(new GuiYesNo(this, "Reset to Defaults", "Are you sure?", 0));
                break;


            case 20: {
                if (mc.world != null)
                    for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
                        if (EntityList.createEntityByIDFromName(resourceLocation, mc.world) instanceof IAnimals && !(EntityList.createEntityByIDFromName(resourceLocation, mc.world) instanceof IMob)) {
                            if (!listGui.list.contains(resourceLocation.toString()))
                                listGui.list.add(resourceLocation.toString());
                        }
                    }
                break;
            }

            case 3:
                mc.displayGuiScreen(Main.ClickGui);
                break;


            case 40:
                if (mc.world != null)
                    for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
                        if (EntityList.createEntityByIDFromName(resourceLocation, mc.world) instanceof IMob) {
                            if (!listGui.list.contains(resourceLocation.toString()))
                                listGui.list.add(resourceLocation.toString());
                        }
                    }
                break;
            default:
                break;
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (id == 0 && result) {
            mobs.clear();
        }
        super.confirmClicked(result, id);
        mc.displayGuiScreen(this);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        listGui.handleMouseInput(mouseX, mouseY);
        AllMobs.handleMouseInput(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
            throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        MobFieldName.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseX < 550 || mouseX > width - 50 || mouseY < 32 || mouseY > height - 64) {
            listGui.selected = -1;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        MobFieldName.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN)
            actionPerformed(addButton);
        else if (keyCode == Keyboard.KEY_DELETE)
            actionPerformed(removeButton);
        else if (keyCode == Keyboard.KEY_ESCAPE)
            actionPerformed(doneButton);
        if (!MobFieldName.getText().isEmpty())
            AllMobs.list = MobSearch(MobFieldName.getText());
        else
            AllMobs.list = RemoveMobs(mobs);
    }

    public static ArrayList<String> Getmobs() {
        return new ArrayList<>(listGui.list);
    }

    @Override
    public void updateScreen() {
        MobFieldName.updateCursorCounter();
        if (listGui.selected >= 0 && listGui.selected <= listGui.list.size())
            MobToRemove = listGui.list.get(listGui.selected);

        if (AllMobs.selected >= 0 && AllMobs.selected < AllMobs.list.size())
            MobToAdd = AllMobs.list.get(AllMobs.selected);

        addButton.enabled = MobToAdd != null;
        removeButton.enabled = listGui.selected >= 0 && listGui.selected < listGui.list.size();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(mc.fontRenderer, "Mob" + " (" + listGui.getSize() + ")", width / 2, 12, 0xffffff);

        listGui.drawScreen(mouseX, mouseY, partialTicks);

        AllMobs.drawScreen(mouseX, mouseY, partialTicks);

        MobFieldName.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (MobFieldName.getText().isEmpty() && !MobFieldName.isFocused())
            drawString(mc.fontRenderer, "Mob name", 68,
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

    }

    private static class ListGui extends GuiScrollingList {
        private final Minecraft mc;
        private List<String> list;
        private int selected = -1;
        private int offsetx;

        public ListGui(Minecraft mc, EspGui screen, List<String> list, int offsetx, int offsety) {
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

            FontRenderer fr = mc.fontRenderer;

            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            try {
                int scale = 13;
                if (name.contains("giant") || name.contains("ghast") || name.contains("ender_dragon"))
                    scale = 5;
                GuiInventory.drawEntityOnScreen(58 + offsetx, slotTop + 13, scale, 0.0F, 0.0f, (EntityLivingBase) Objects.requireNonNull(EntityList.createEntityByIDFromName(new ResourceLocation(name), mc.world)));
            } catch (Exception ignored) {
            }
            GlStateManager.popMatrix();


            fr.drawString(" (" + name + ")", 68 + offsetx, slotTop + 2, 0xf0f0f0);
        }


    }
}
