package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Fovmod extends Module {
    /////////////////////
    public Fovmod() {
        super("Fovmod", Keyboard.KEY_NONE, Category.RENDER, "Fovmod");
    }

    public float defaultFov;

    Setting Change = setmgr.add(new Setting("Change", this, 100, 0, 500, true));
    Setting Smooth = setmgr.add(new Setting("Smooth", this, true));
    Setting FovMode = setmgr.add(new Setting("FovMode", this, "ViewModelChanger", "ViewModelChanger", "FovChanger", "Zoom"));


    @Override
    public void FOVModifier(EntityViewRenderEvent.FOVModifier event) {
        if (FovMode.getValString().equalsIgnoreCase("ViewModelChanger")) {
            event.setFOV((float) Change.getValDouble());
        }
    }

    @Override
    public void onEnable() {
        defaultFov = mc.gameSettings.fovSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.fovSetting = defaultFov;
        mc.gameSettings.smoothCamera = false;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        mc.gameSettings.smoothCamera = Smooth.getValBoolean();
        if (FovMode.getValString().equalsIgnoreCase("FovChanger")) {
            mc.gameSettings.fovSetting = (float) Change.getValDouble();
        }
        if (FovMode.getValString().equalsIgnoreCase("Zoom")) {
            if (mc.gameSettings.fovSetting > 12f) {
                for (int i = 0; i < Change.getValDouble(); i++) {
                    if (mc.gameSettings.fovSetting > 12f) {
                        mc.gameSettings.fovSetting -= 0.1f;
                    }
                }
            } else if (mc.gameSettings.fovSetting < this.defaultFov) {
                for (int i = 0; i < Change.getValDouble(); i++) {
                    mc.gameSettings.fovSetting += 0.1F;
                }
            }
        }
    }

}
