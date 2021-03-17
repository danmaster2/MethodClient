package Method.Client.utils.proxy.renderers;

import Method.Client.module.movement.BoatFly;
import Method.Client.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityBoat;

import static Method.Client.utils.visual.ColorUtils.glColor;

public class ModRenderBoat extends RenderBoat {

    public ModRenderBoat(RenderManager renderManager) {
        super(renderManager);
    }
    @Override
    public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (BoatFly.Boatblend.getValBoolean())
            GlStateManager.enableBlend();

        if (BoatFly.BoatRender.getValString().equalsIgnoreCase("Vanish"))
            return;
        if (BoatFly.BoatRender.getValString().equalsIgnoreCase("Rainbow"))
            glColor(ColorUtils.rainbow().getRGB());


        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        if (BoatFly.Boatblend.getValBoolean())
            GlStateManager.disableBlend();
    }
}