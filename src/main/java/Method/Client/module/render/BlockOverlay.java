package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class BlockOverlay extends Module {

    Setting OverlayColor = setmgr.add(new Setting("OverlayColor", this, 0, 1, 1, .62));
    Setting Mode = setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    
    public BlockOverlay() {
        super("BlockOverlay", Keyboard.KEY_NONE, Category.RENDER, "BlockOverlay");
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.objectMouseOver == null)
            return;

        if (Block.getIdFromBlock(mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()) == 0)
            return;

        if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            RenderBlock(Mode.getValString(), Standardbb(blockPos), OverlayColor.getcolor(), LineWidth.getValDouble());
        }

        super.onRenderWorldLast(event);
    }

}
