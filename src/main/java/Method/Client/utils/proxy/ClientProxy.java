package Method.Client.utils.proxy;

import Method.Client.Main;
import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.ColorMix;
import Method.Client.utils.proxy.Overrides.EntityRenderMixin;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = Side.CLIENT, modid = Main.MODID)
public class ClientProxy implements IProxy {
    public static Module Gl;
    protected static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void init(FMLInitializationEvent event) {
        ColorMix.replaceRenderers();
        ViewBobOverride();
    }

    public static void ViewBobOverride() {
        mc.entityRenderer = new EntityRenderMixin(mc, mc.resourceManager);
    }

}