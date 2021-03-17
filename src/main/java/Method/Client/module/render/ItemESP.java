package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;

public class ItemESP extends Module {
    Setting OverlayColor = setmgr.add(new Setting("OverlayColor", this, 0, 1, 1, 1));
    Setting Mode = setmgr.add(new Setting("Drop Mode", this, "Outline", BlockEspOptions()));
    Setting Glow = setmgr.add(new Setting("Glow", this, false));
    Setting LifeSpan = setmgr.add(new Setting("Thrower", this, true));
    Setting Nametag = setmgr.add(new Setting("Nametag", this, true));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    Setting Scale = setmgr.add(new Setting("Scale", this, .1, 0, 1, false));


    public ItemESP() {
        super("ItemESP", Keyboard.KEY_NONE, Category.RENDER, "Droped item glow");
    }

    @Override
    public void onDisable() {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityItem) {
                Entity item = (Entity) object;
                item.setGlowing(false);
            }
        }
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityItem) {
                EntityItem item = (EntityItem) object;
                double S = Scale.getValDouble();
                RenderBlock(Mode.getValString(), RenderUtils.Boundingbb(item, -S, +.2 - S, -S, +S, +.2 + S, +S), OverlayColor.getcolor(), LineWidth.getValDouble());
                if (Nametag.getValBoolean())
                    RenderUtils.SimpleNametag(item.getPositionVector(), item.getItem().getDisplayName() + " x" + item.getItem().getCount() + (LifeSpan.getValBoolean() ? (" " + (item.lifespan - item.age) / 20) + " S" : ""));
                item.setGlowing(Glow.getValBoolean());
            }
        }
        super.onRenderWorldLast(event);
    }


}
