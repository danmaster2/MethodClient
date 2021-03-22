package Method.Client.utils.Screens.Custom.Packet;

import Method.Client.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class AntiPacketGui extends GuiScreen {

    private static ListGui listGui;
    private ListGui AllMobs;
    private GuiTextField MobFieldName;
    private GuiButton addButton;
    private GuiButton removeButton;
    private GuiButton doneButton;
    private String PacketToAdd;
    private String PacketToRemove;

    public static ArrayList<String> Packets = new ArrayList<>();
    public static ArrayList<String> AllPackets = new ArrayList<>();

    public static ArrayList<AntiPacketPacket> ListOfPackets = new ArrayList<>();


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static List<String> removePackets(List<String> input) {
        AllPackets.clear();
        for (AntiPacketPacket listOfPacket : ListOfPackets) {
            if (!input.contains(listOfPacket.packet.getClass().getSimpleName()))
                AllPackets.add(listOfPacket.packet.getClass().getSimpleName());
        }
        return AllPackets;
    }


    public static List<String> PacketSearch(String text) {
        ArrayList<String> Temp = new ArrayList<>();
        for (AntiPacketPacket listOfPacket : ListOfPackets) {
            String s = listOfPacket.packet.getClass().getSimpleName();
            if (listOfPacket.packet.getClass().getSimpleName().toLowerCase().contains(text.toLowerCase()))
                Temp.add(s);
        }
        return Temp;
    }

    @Override
    public void initGui() {
        ListSetup();
        listGui = new ListGui(mc, this, Packets, width - (width / 3), 0);

        AllMobs = new ListGui(mc, this, removePackets(Packets), 0, 0);

        MobFieldName = new GuiTextField(1, mc.fontRenderer, 64, height - 55, 150, 18);

        buttonList.add(addButton = new GuiButton(0, 214, height - 56, 30, 20, "Add"));

        buttonList.add(removeButton = new GuiButton(1, width - 300, height - 56, 100, 20, "Remove Selected"));

        buttonList.add(new GuiButton(2, width - 108, 8, 100, 20, "Remove All"));

        buttonList.add(new GuiButton(20, width - 308, 8, 100, 20, "Add Server"));

        buttonList.add(new GuiButton(40, width - 208, 8, 100, 20, "Add Client"));

        buttonList.add(doneButton = new GuiButton(3, width / 2 - 100, height - 28, "Done"));
    }

    public static ArrayList<AntiPacketPacket> GetPackets() {
        ArrayList<AntiPacketPacket> list = new ArrayList<>();
        for (AntiPacketPacket listOfPacket : ListOfPackets) {
            for (String s : listGui.list) {
                if (s.equalsIgnoreCase(listOfPacket.packet.getClass().getSimpleName())) {
                    list.add(listOfPacket);
                    break;
                }
            }
        }
        return list;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch (button.id) {
            case 0:
                if ((AllMobs.selected >= 0 && AllMobs.selected <= AllMobs.list.size())) {
                    if (!PacketToAdd.isEmpty()) {
                        if (!Packets.contains(PacketToAdd)) {
                            Packets.add(PacketToAdd);
                            AllPackets.remove(PacketToAdd);
                            PacketToAdd = "";
                        }
                    }
                }
                break;
            case 1:
                Packets.remove(listGui.selected);
                AllPackets.add(PacketToRemove);
                break;
            case 2:
                mc.displayGuiScreen(new GuiYesNo(this, "Reset to Defaults", "Are you sure?", 0));
                break;


            case 20: {
                if (mc.world != null)
                    for (AntiPacketPacket listOfPacket : ListOfPackets) {
                        if (listOfPacket.packet.getClass().getSimpleName().startsWith("S")) {
                            if (!listGui.list.contains(listOfPacket.packet.getClass().getSimpleName()))
                                listGui.list.add(listOfPacket.packet.getClass().getSimpleName());
                        }
                    }
                break;
            }

            case 3:
                mc.displayGuiScreen(Main.ClickGui);
                break;


            case 40:
                if (mc.world != null)
                    for (AntiPacketPacket listOfPacket : ListOfPackets) {
                        if (listOfPacket.packet.getClass().getSimpleName().startsWith("C")) {
                            if (!listGui.list.contains(listOfPacket.packet.getClass().getSimpleName()))
                                listGui.list.add(listOfPacket.packet.getClass().getSimpleName());
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
            Packets.clear();
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
            AllMobs.list = PacketSearch(MobFieldName.getText());
        else
            AllMobs.list = removePackets(Packets);
    }

    public static ArrayList<String> Getmobs() {
        return new ArrayList<>(listGui.list);
    }

    @Override
    public void updateScreen() {
        MobFieldName.updateCursorCounter();
        if (listGui.selected >= 0 && listGui.selected <= listGui.list.size())
            PacketToRemove = listGui.list.get(listGui.selected);

        if (AllMobs.selected >= 0 && AllMobs.selected < AllMobs.list.size())
            PacketToAdd = AllMobs.list.get(AllMobs.selected);

        addButton.enabled = PacketToAdd != null;
        removeButton.enabled = listGui.selected >= 0 && listGui.selected < listGui.list.size();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(mc.fontRenderer, "Packet" + " (" + listGui.getSize() + ")", width / 2, 12, 0xffffff);

        listGui.drawScreen(mouseX, mouseY, partialTicks);

        AllMobs.drawScreen(mouseX, mouseY, partialTicks);

        MobFieldName.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (MobFieldName.getText().isEmpty() && !MobFieldName.isFocused())
            drawString(mc.fontRenderer, "Packet Name", 68,
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

        public ListGui(Minecraft mc, AntiPacketGui screen, List<String> list, int offsetx, int offsety) {
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
            FontRenderer fr = mc.fontRenderer;
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
            fr.drawString(" (" + list.get(slotIdx) + ")", 68 + offsetx, slotTop + 2, 0xf0f0f0);
        }


    }

    private void ListSetup() {
        ListOfPackets.clear();
        ListOfPackets.add(new AntiPacketPacket(new CPacketEncryptionResponse()));
        ListOfPackets.add(new AntiPacketPacket(new C00Handshake()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketLoginStart()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketDisconnect()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEnableCompression()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEncryptionRequest()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketLoginSuccess()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketAnimation()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketChatMessage()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketClickWindow()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketClientSettings()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketClientStatus()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketCloseWindow()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketConfirmTeleport()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketConfirmTransaction()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketCreativeInventoryAction()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketCustomPayload()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketEnchantItem()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketEntityAction()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketHeldItemChange()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketInput()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketKeepAlive()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPlaceRecipe()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPlayer()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPlayerAbilities()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPlayerDigging()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPlayerTryUseItem()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPing()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketPlayerTryUseItemOnBlock()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketRecipeInfo()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketResourcePackStatus()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketServerQuery()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketSeenAdvancements()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketSpectate()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketSteerBoat()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketTabComplete()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketUpdateSign()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketUseEntity()));
        ListOfPackets.add(new AntiPacketPacket(new CPacketVehicleMove()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketAdvancementInfo()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketAnimation()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketBlockAction()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketBlockBreakAnim()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketBlockChange()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketCamera()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketChangeGameState()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketChat()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketChunkData()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketCloseWindow()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketCollectItem()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketCombatEvent()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketConfirmTransaction()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketCooldown()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketCustomPayload()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketDestroyEntities()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketDisconnect()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketDisplayObjective()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEffect()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntity()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityAttach()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityEffect()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityEquipment()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityHeadLook()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityMetadata()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityProperties()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityStatus()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityTeleport()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketEntityVelocity()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketExplosion()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketHeldItemChange()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketJoinGame()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketKeepAlive()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketMaps()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketMoveVehicle()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketMultiBlockChange()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketOpenWindow()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketParticles()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketPlaceGhostRecipe()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketPlayerAbilities()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketPlayerListHeaderFooter()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketPlayerListItem()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketPlayerPosLook()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketRecipeBook()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketRemoveEntityEffect()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketResourcePackSend()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketRespawn()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketScoreboardObjective()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSelectAdvancementsTab()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketServerDifficulty()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSetExperience()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSetPassengers()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSetSlot()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSignEditorOpen()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSoundEffect()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnExperienceOrb()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnGlobalEntity()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnMob()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnObject()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnPainting()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnPlayer()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketSpawnPosition()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketStatistics()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketTabComplete()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketTeams()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketTimeUpdate()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketTitle()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketUnloadChunk()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketUpdateBossInfo()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketUpdateHealth()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketUpdateScore()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketUpdateTileEntity()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketUseBed()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketWindowItems()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketWindowProperty()));
        ListOfPackets.add(new AntiPacketPacket(new SPacketWorldBorder()));
    }
}
