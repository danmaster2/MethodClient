package Method.Client.module.movement;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class AutoSwim extends Module {
    Setting mode = setmgr.add(mode = new Setting("Mode", this, "Dolphin", "Dolphin", "Jump", "Fish"));

    public AutoSwim() {
        super("Auto Swim", Keyboard.KEY_NONE, Category.MOVEMENT, "Swims for you");
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (!mc.player.isInWater() && !mc.player.isInLava()) {
            return;
        }
        if (mc.player.isSneaking() || Wrapper.INSTANCE.mcSettings().keyBindJump.isKeyDown()) {
            return;
        }
        if (mode.getValString().equalsIgnoreCase("Jump")) {
            mc.player.jump();
        } else if (mode.getValString().equalsIgnoreCase("Dolphin")) {
            mc.player.motionY += 0.04f;
        } else if (mode.getValString().equalsIgnoreCase("Fish")) {
            mc.player.motionY += 0.02f;
        }
        super.onClientTick(event);
    }
}
