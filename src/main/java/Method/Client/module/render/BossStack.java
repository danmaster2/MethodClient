package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Method.Client.Main.setmgr;

public class BossStack extends Module {

    Setting mode = setmgr.add(new Setting("Stack mode", this, "Stack", "Stack", "Minimize"));
    Setting scale = setmgr.add(new Setting("Scale", this, .5, .5, 4, false));

    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");

    public BossStack() {
        super("BossStack", Keyboard.KEY_NONE, Category.RENDER, "BossStack");
    }

    @Override
    public void RenderGameOverLayPost(RenderGameOverlayEvent.Post event) {
        if (!(event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO))
            return;
        if (mode.getValString().equalsIgnoreCase("Minimize")) {
            Map<UUID, BossInfoClient> map = mc.ingameGUI.getBossOverlay().mapBossInfos;
            if (map == null) return;
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaledWidth();
            int j = 12;

            for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
                BossInfoClient info = entry.getValue();
                String text = info.getName().getFormattedText();

                j = Collapse(event, i, j, info, text);
            }
        } else if (mode.getValString().equalsIgnoreCase("Stack")) {
            Map<UUID, BossInfoClient> map = mc.ingameGUI.getBossOverlay().mapBossInfos;
            HashMap<String, Pair<BossInfoClient, Integer>> to = new HashMap<>();

            for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
                String s = entry.getValue().getName().getFormattedText();
                Pair<BossInfoClient, Integer> p;
                if (to.containsKey(s)) {
                    p = to.get(s);
                    p = new Pair<>(p.getKey(), p.getValue() + 1);
                } else {
                    p = new Pair<>(entry.getValue(), 1);
                }
                to.put(s, p);
            }

            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaledWidth();
            int j = 12;

            for (Map.Entry<String, Pair<BossInfoClient, Integer>> entry : to.entrySet()) {
                String text = entry.getKey();
                BossInfoClient info = entry.getValue().getKey();
                int a = entry.getValue().getValue();
                text += " x" + a;

                j = Collapse(event, i, j, info, text);
            }
        }

    }

    @Override
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO) {
            event.setCanceled(true);
        }
    }


    private int Collapse(RenderGameOverlayEvent.Post event, int i, int j, BossInfoClient info, String text) {
        int k = (int) ((i / scale.getValDouble()) / 2 - 91);
        GlStateManager.scale(scale.getValDouble(), scale.getValDouble(), 1);
        if (!event.isCanceled()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
            mc.ingameGUI.getBossOverlay().render(k, j, info);
            mc.fontRenderer.drawStringWithShadow(text, (float) ((i / scale.getValDouble()) / 2 - mc.fontRenderer.getStringWidth(text) / 2), (float) (j - 9), 16777215);
        }
        GlStateManager.scale(1d / scale.getValDouble(), 1d / scale.getValDouble(), 1);
        j += 10 + mc.fontRenderer.FONT_HEIGHT;
        return j;
    }


}

class Pair<T, S> {
    T key;
    S value;

    public Pair(T key, S value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public S getValue() {
        return value;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public void setValue(S value) {
        this.value = value;
    }
}
