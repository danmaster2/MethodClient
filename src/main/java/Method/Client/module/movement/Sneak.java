package Method.Client.module.movement;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.isMoving;

public class Sneak extends Module {
    Setting mode = setmgr.add( new Setting("Mode", this, "Legit", "Legit", "Packet"));
    Setting Antisneak = setmgr.add( new Setting("Antisneak", this, false));
    Setting fullSprint = setmgr.add( new Setting("FullSprint", this, false, Antisneak, 2));

    public Sneak() {
        super("Sneak", Keyboard.KEY_NONE, Category.MOVEMENT, "Sneak");
    }

    @Override
    public void onDisable() {
        if (mode.getValString().equalsIgnoreCase("legit"))
            mc.gameSettings.keyBindSneak.pressed = false;

        if (mode.getValString().equalsIgnoreCase("Packet")) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (Antisneak.getValBoolean()) {
            EntityPlayerSP player = mc.player;
            GameSettings settings = Wrapper.INSTANCE.mcSettings();
            if (player.onGround && settings.keyBindSneak.isKeyDown()) {
                if (!fullSprint.getValBoolean() && settings.keyBindForward.isKeyDown()) {
                    player.setSprinting(Utils.isMoving(player));
                } else if (fullSprint.getValBoolean()) {
                    player.setSprinting(Utils.isMoving(player));
                }
                if (settings.keyBindRight.isKeyDown()
                        || settings.keyBindLeft.isKeyDown()
                        || settings.keyBindBack.isKeyDown()) {
                    if (settings.keyBindBack.isKeyDown()) {
                        player.motionX *= 1.268;
                        player.motionZ *= 1.268;
                    } else {
                        player.motionX *= 1.252;
                        player.motionZ *= 1.252;
                    }
                } else {
                    player.motionX *= 1.2848;
                    player.motionZ *= 1.2848;
                }
            }
        }
        if (mode.getValString().equalsIgnoreCase("Legit")) {
            mc.gameSettings.keyBindSneak.pressed = true;

        }

        if (mode.getValString().equalsIgnoreCase("Packet")) {
            EntityPlayerSP player = mc.player;
            if (!isMoving(mc.player)) {
                player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
                player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            if (isMoving(mc.player)) {
                player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.STOP_SNEAKING));
                player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_SNEAKING));
            }
        }
        super.onClientTick(event);
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (Antisneak.getValBoolean()) {
            if (side == Connection.Side.OUT && packet instanceof CPacketEntityAction) {
                CPacketEntityAction p = (CPacketEntityAction) packet;
                return p.getAction() != CPacketEntityAction.Action.STOP_SNEAKING;
            }
        }
        return true;
    }


}

