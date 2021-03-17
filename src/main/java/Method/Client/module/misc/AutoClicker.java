
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class AutoClicker extends Module {

    /////////////////////

    public AutoClicker() {
        super("AutoClicker", Keyboard.KEY_NONE, Category.MISC, "Auto Clicker");
    }

    Setting Type = setmgr.add(new Setting("Click Mode", this, "Left", "Left", "Right"));
    Setting Delay = setmgr.add(new Setting("Delay", this, .2, 0, 5, false));
    Setting Hold = setmgr.add(new Setting("Hold", this, false));
    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Hold.getValBoolean()) {
            mc.gameSettings.keyBindAttack.pressed = false;
            mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (Type.getValString().equalsIgnoreCase("Left") && timer.isDelay((long) Delay.getValDouble() * 1000)) {
            mc.gameSettings.keyBindAttack.pressed = true;
            this.timer.setLastMS();
        }
        if (Type.getValString().equalsIgnoreCase("Right") && timer.isDelay((long) Delay.getValDouble() * 1000)) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            this.timer.setLastMS();
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindAttack.pressed = false;
        mc.gameSettings.keyBindUseItem.pressed = false;
    }
}
