package Method.Client.module.Onscreen;

import Method.Client.clickgui.component.Component;
import Method.Client.clickgui.component.Frame;
import Method.Client.module.Category;
import Method.Client.module.Onscreen.Display.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.io.IOException;
import java.util.ArrayList;

public class OnscreenGUI extends GuiScreen {
    public static final ArrayList<PinableFrame> pinableFrames = new ArrayList<>();

    private final Frame Onscreen;

    public OnscreenGUI() {
        Onscreen = new Frame(Category.ONSCREEN);
        Onscreen.setOpen(true);
        pinableFrames.add(new Angles.AnglesRUN());
        pinableFrames.add(new Player.PlayerRUN());
        pinableFrames.add(new EnabledMods.EnabledModsRUN());
        pinableFrames.add(new Armor.ArmorRUN());
        pinableFrames.add(new Biome.BiomeRUN());
        pinableFrames.add(new Blockview.BlockviewRUN());
        pinableFrames.add(new Durability.DurabilityRUN());
        pinableFrames.add(new Coords.CoordsRUN());
        pinableFrames.add(new Direction.DirectionRUN());
        pinableFrames.add(new Fps.FpsRUN());
        pinableFrames.add(new CombatItems.CombatItemsRUN());
        pinableFrames.add(new ChunkSize.ChunkSizeRUN());
        pinableFrames.add(new Inventory.InventoryRUN());
        pinableFrames.add(new NetherCords.NetherCordsRUN());
        pinableFrames.add(new Ping.PingRUN());
        pinableFrames.add(new Hole.HoleRUN());
        pinableFrames.add(new PlayerCount.PlayerCountRUN());
        pinableFrames.add(new Server.ServerRUN());
        pinableFrames.add(new PlayerSpeed.SpeedRUN());
        pinableFrames.add(new KeyStroke.KeyStrokeRUN());
        pinableFrames.add(new Time.TimeRUN());
        pinableFrames.add(new Tps.TpsRUN());
        pinableFrames.add(new Hunger.HungerRUN());
        pinableFrames.add(new Potions.PotionsRUN());
        pinableFrames.add(new Enemypos.EnemyposRUN());
        pinableFrames.add(new ServerResponce.ServerResponceRUN());
    }

    protected Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void updateScreen() {
        super.updateScreen();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        if (Onscreen.isWithinBounds(mouseX, mouseY))
            Onscreen.handleScrollinput();

        Onscreen.updatePosition(mouseX, mouseY);
        Onscreen.renderFrame();

        if (Onscreen.isOpen())
            for (Component comp : Onscreen.getComponents()) {
                comp.RenderTooltip();
                try {
                    comp.updateComponent(mouseX, mouseY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        for (PinableFrame pinableFrame : pinableFrames) {

            if (mc.currentScreen instanceof OnscreenGUI) {
                pinableFrame.renderFrame();
                pinableFrame.Ongui();
            }
            pinableFrame.renderFrameText();
            pinableFrame.updatePosition(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (Onscreen.isWithinHeader(mouseX, mouseY) && mouseButton == 0) {
            Onscreen.setDrag(true);
            Onscreen.dragX = mouseX - Onscreen.getX();
            Onscreen.dragY = mouseY - Onscreen.getY();
        }
        if (Onscreen.isWithinHeader(mouseX, mouseY) && mouseButton == 1) {
            Onscreen.setOpen(!Onscreen.isOpen());
        }
        if (Onscreen.isOpen()) {
            if (!Onscreen.getComponents().isEmpty()) {
                if (Onscreen.isWithinBounds(mouseX, mouseY))
                    for (Component component : Onscreen.getComponents()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
            }
        }

        if (Onscreen.isWithinFooter(mouseX, mouseY) && mouseButton == 0) {
            Onscreen.dragScrollstop = mouseY - Onscreen.getScrollpos();
            Onscreen.setDragBot(true);
        }


        boolean multidrag = false;
        for (PinableFrame pinableFrame : pinableFrames) {
            if (pinableFrame.isWithinHeader(mouseX, mouseY) && mouseButton == 0 && !multidrag && pinableFrame.isPinned()) {
                pinableFrame.setDrag(true);
                pinableFrame.dragX = mouseX - pinableFrame.getX();
                pinableFrame.dragY = mouseY - pinableFrame.getY();
                multidrag = true;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        Onscreen.setDrag(false);
        Onscreen.setDragBot(false);

        if (Onscreen.isOpen()) {
            if (!Onscreen.getComponents().isEmpty()) {
                for (Component component : Onscreen.getComponents()) {
                    component.mouseReleased(mouseX, mouseY, state);
                }
            }

        }
        for (PinableFrame pinableFrame : pinableFrames) {
            pinableFrame.setDrag(false);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (Onscreen.isOpen() && keyCode != 1) {
            if (!Onscreen.getComponents().isEmpty()) {
                for (Component component : Onscreen.getComponents()) {
                    component.keyTyped(typedChar, keyCode);
                }
            }
        }
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        for (PinableFrame frame : OnscreenGUI.pinableFrames) {
            if (frame.isPinned())
                frame.onRenderGameOverlay(event);
        }
    }
}