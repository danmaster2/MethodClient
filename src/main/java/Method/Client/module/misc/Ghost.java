
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Screens.Override.DeathOverride;
import Method.Client.utils.system.Wrapper;
import net.minecraft.network.play.client.CPacketClientStatus;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Ghost extends Module {

    /////////////////////
    public static Setting health;
    public Ghost() {
        super("Ghost", Keyboard.KEY_NONE, Category.MISC, "Move While dead");
    }

    @Override
    public void setup() {
        setmgr.add(health = new Setting("health", this, 40, 0, 40, true));
    }

    @Override
    public void onEnable() {
        DeathOverride.Override = true;
    }

    @Override
    public void onDisable() {
        DeathOverride.Override = false;
        if (DeathOverride.isDead) {
            Wrapper.INSTANCE.sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            DeathOverride.isDead = false;
        }
    }
}
