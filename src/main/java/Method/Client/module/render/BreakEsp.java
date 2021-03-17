package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class BreakEsp extends Module {

    public BreakEsp() {
        super("BreakEsp", Keyboard.KEY_NONE, Category.RENDER, "BreakEsp");
    }

    Setting ignoreSelf = setmgr.add( new Setting("ignoreSelf", this, false));
    Setting onlyObsi = setmgr.add( new Setting("onlyObsi", this, false));
    Setting fade = setmgr.add( new Setting("fade", this, true));
    Setting Mode = setmgr.add( new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add( new Setting("LineWidth", this, 1, 0, 3, false));
    Setting OverlayColor = setmgr.add( new Setting("OverlayColor", this, 0, 1, 1, .56));
    Setting Distance = setmgr.add( new Setting("Distance", this, 10, 0, 50, false));


    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        mc.renderGlobal.damagedBlocks.forEach((var1x, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {
                if (Distance.getValDouble() * 5 > mc.player.getDistanceSqToCenter(destroyBlockProgress.getPosition()))
                    if (!ignoreSelf.getValBoolean() || mc.world.getEntityByID(var1x) != mc.player) {
                        if (!onlyObsi.getValBoolean() || mc.world.getBlockState(destroyBlockProgress.getPosition()).getBlock() == Blocks.OBSIDIAN) {
                            AxisAlignedBB pos = Standardbb(destroyBlockProgress.getPosition());
                            if (fade.getValBoolean())
                                pos = pos.shrink((3 - (destroyBlockProgress.getPartialBlockDamage()) / (2.0 + (2.0 / 3.0))) / 9.0);
                            RenderBlock(Mode.getValString(), pos, OverlayColor.getcolor(), LineWidth.getValDouble());
                        }
                    }
            }
        });
        super.onRenderWorldLast(event);
    }

}
