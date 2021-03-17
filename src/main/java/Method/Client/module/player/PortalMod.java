package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class PortalMod extends Module {
    Setting gui = setmgr.add(new Setting("gui", this, true));
    Setting god = setmgr.add(new Setting("god", this, true));

    public PortalMod() {
        super("PortalMod", Keyboard.KEY_NONE, Category.PLAYER, "PortalMod");
    }

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (gui.getValBoolean())
            mc.player.inPortal = false;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (god.getValBoolean())
            return !(packet instanceof CPacketConfirmTeleport);
        return true;
    }
}
