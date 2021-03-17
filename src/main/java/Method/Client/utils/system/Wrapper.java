package Method.Client.utils.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumHand;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class Wrapper {

    public static FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    public static volatile Wrapper INSTANCE = new Wrapper();
    public static Minecraft mc = Minecraft.getMinecraft();

    public Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public EntityPlayerSP player() {
        return Wrapper.INSTANCE.mc().player;
    }

    public WorldClient world() {
        return Wrapper.INSTANCE.mc().world;
    }

    public GameSettings mcSettings() {
        return Wrapper.INSTANCE.mc().gameSettings;
    }

    public FontRenderer fontRenderer() {
        return Wrapper.INSTANCE.mc().fontRenderer;
    }

    public void sendPacket(Packet packet) {
        this.player().connection.sendPacket(packet);
    }

    public PlayerControllerMP controller() {
        return Wrapper.INSTANCE.mc().playerController;
    }

    public void swingArm() {
        this.player().swingArm(EnumHand.MAIN_HAND);
    }

    public void attack(Entity entity) {
        controller().attackEntity(player(), entity);
    }

    public void copy(String content) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content), null);
    }
}
