package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;

public class ChunkBorder extends Module {

    Setting OverlayColor = setmgr.add(new Setting("OverlayColor", this, 0, 1, 1, .52));
    Setting Mode = setmgr.add(new Setting("Chunk Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    Setting Height = setmgr.add(new Setting("Height", this, 0, 0, 255, true));
    Setting FollowPlayer = setmgr.add(new Setting("FollowPlayer", this, true));


    public ChunkBorder() {
        super("ChunkBorder", Keyboard.KEY_NONE, Category.RENDER, "ChunkBorder");
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        Chunk chunk = mc.world.getChunk(mc.player.getPosition());
        double renderPosX = (chunk.x * 16) - mc.getRenderManager().viewerPosX;
        double renderPosY = -mc.getRenderManager().viewerPosY;
        double renderPosZ = (chunk.z * 16) - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb1;
        if (FollowPlayer.getValBoolean())
            bb1 = new AxisAlignedBB(renderPosX, renderPosY + mc.player.posY, renderPosZ, renderPosX + 16, renderPosY + 1.0 + mc.player.posY, renderPosZ + 16);
        else
            bb1 = new AxisAlignedBB(renderPosX, renderPosY + Height.getValDouble(), renderPosZ, renderPosX + 16, renderPosY + 1.0 + Height.getValDouble(), renderPosZ + 16);

        RenderBlock(Mode.getValString(), bb1, OverlayColor.getcolor(), LineWidth.getValDouble());

    }
}
