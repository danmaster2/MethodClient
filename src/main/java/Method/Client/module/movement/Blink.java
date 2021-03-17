package Method.Client.module.movement;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Entity301;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Blink extends Module {

    public Entity301 entity301 = null;

    public Blink() {
        super("Blink", Keyboard.KEY_NONE, Category.MOVEMENT, "Cancel packets and move");
    }

    Setting Limit = setmgr.add(new Setting("Packet limit", this, 0, 0, 500, true));
    Setting Renable = setmgr.add(new Setting("Renable", this, false));
    Setting ShowPos = setmgr.add(new Setting("ShowPos", this, true));
    Setting Entity = setmgr.add(new Setting("Entity", this, true));

    int limitcount = 0;

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT && packet instanceof CPacketVehicleMove && Entity.getValBoolean()) {
            limitcount++;
            if (Limit.getValDouble() == 0)
                return false;
            if (Limit.getValDouble() < limitcount) {
                limitcount = 0;
                onoff();
                return true;
            }
        }
        if (side == Connection.Side.OUT && packet instanceof CPacketPlayer) {
            limitcount++;
            if (Limit.getValDouble() == 0)
                return false;
            if (Limit.getValDouble() < limitcount) {
                limitcount = 0;
                onoff();
                return true;
            }
        }
        return true;
    }

    @Override
    public void onEnable() {
        Enable();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        limitcount = 0;
        if (this.entity301 != null && mc.world != null) {
            mc.world.removeEntity(this.entity301);
            this.entity301 = null;
        }
        super.onDisable();
    }

    private void onoff() {
        limitcount = 0;
        if (this.entity301 != null && mc.world != null) {
            mc.world.removeEntity(this.entity301);
            this.entity301 = null;
        }
        if (Renable.getValBoolean()) {
            Enable();
        }

    }

    private void Enable() {
        limitcount = 0;
        if (mc.player != null && mc.world != null && ShowPos.getValBoolean()) {
            this.entity301 = new Entity301(mc.world, mc.player.getGameProfile());
            this.entity301.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
            this.entity301.inventory = mc.player.inventory;
            this.entity301.rotationPitch = mc.player.rotationPitch;
            this.entity301.rotationYaw = mc.player.rotationYaw;
            this.entity301.rotationYawHead = mc.player.rotationYawHead;
            mc.world.spawnEntity(this.entity301);
        }
    }

}
