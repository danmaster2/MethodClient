package Method.Client.module.combat;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class AutoRespawn extends Module {
    Setting DeathCoords = setmgr.add(new Setting("DeathCoords", this, true));
    Setting Delay = setmgr.add(new Setting("Delay", this, 2, 0, 50, true));
    private TimerUtils timer = new TimerUtils();
    boolean canrespawn = false;

    public AutoRespawn() {
        super("AutoRespawn", Keyboard.KEY_NONE, Category.COMBAT, "AutoRespawn");

    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Wrapper.mc.currentScreen instanceof GuiGameOver) {
            if (!canrespawn) {
                this.timer.reset();
                canrespawn = true;
            }
            if (timer.hasReached((float) (Delay.getValDouble() * 1000L))) {
                this.timer.reset();
                mc.player.respawnPlayer();
                Wrapper.mc.displayGuiScreen(null);
                if (DeathCoords.getValBoolean()) {
                    ChatUtils.message((String.format("you have died at x %d y %d z %d", (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ)));
                }
                canrespawn = false;
            }
        }

    }


}
