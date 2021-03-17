package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.ColorUtils.colorcalc;

public class SkyColor extends Module {
    public SkyColor() {
        super("FogColor", Keyboard.KEY_NONE, Category.RENDER, "FogColor");
    }

    Setting Color = setmgr.add(new Setting("Color", this, .22, 1, 1, 1));


    @Override
    public void fogDensity(EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0f);
        event.setCanceled(true);
    }

    @Override
    public void fogColor(EntityViewRenderEvent.FogColors event) {
        mc.entityRenderer.fogColorRed = colorcalc(Color.getcolor(), 16) / 255.0f;
        mc.entityRenderer.fogColorGreen = colorcalc(Color.getcolor(), 8) / 255.0f;
        mc.entityRenderer.fogColorBlue = colorcalc(Color.getcolor(), 0) / 255.0f;
        event.setRed(colorcalc(Color.getcolor(), 16));
        event.setGreen(colorcalc(Color.getcolor(), 8));
        event.setBlue(colorcalc(Color.getcolor(), 0));
    }

}
