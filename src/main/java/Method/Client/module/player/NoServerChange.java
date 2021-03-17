package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetSlot;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;

public class NoServerChange extends Module {
    Setting Inventory = setmgr.add(new Setting("Held Item Change", this, false));
    Setting Rotate = setmgr.add(new Setting("Rotate", this, true));

    public NoServerChange() {
        super("NoServerChange", Keyboard.KEY_NONE, Category.PLAYER, "NoServerChange");
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT && packet instanceof SPacketSetSlot)
            if (Inventory.getValBoolean()) {
                int currentSlot = mc.player.inventory.currentItem;
                final SPacketSetSlot packet2 = (SPacketSetSlot) packet;
                if (packet2.getSlot() != currentSlot) {
                    Objects.requireNonNull(mc.networkManager).sendPacket(new CPacketHeldItemChange(currentSlot)); // set server's slot back to our slot
                    MC.gameSettings.keyBindUseItem.pressed = true; // likely will eating so stop right clicking
                    return false;
                }
            }

        if (packet instanceof SPacketPlayerPosLook) {
            if (Rotate.getValBoolean()) {
                final SPacketPlayerPosLook packet3 = (SPacketPlayerPosLook) packet;
                if (mc.player != null) {
                    packet3.yaw = mc.player.rotationYaw;
                    packet3.pitch = mc.player.rotationPitch;

                }
            }
        }

        return true;
    }
}
