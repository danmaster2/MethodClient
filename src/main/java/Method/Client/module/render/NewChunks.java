package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;

public class NewChunks extends Module {
    Setting OverlayColor = setmgr.add( new Setting("OverlayColor", this, 0, 1, 1, 1));
    Setting Mode = setmgr.add( new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    Setting LineWidth = setmgr.add( new Setting("LineWidth", this, 1, 0, 3, false));
    Setting MaxDistance = setmgr.add( new Setting("MaxDistance", this, 1000, 0, 50000, false));


    public NewChunks() {
        super("NewChunks", Keyboard.KEY_NONE, Category.RENDER, "NewChunks");
    }

    private final List<Vec2f> chunkDataList = new ArrayList<>();

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof SPacketChunkData) {
            final SPacketChunkData packet2 = (SPacketChunkData) packet;
            if (!packet2.isFullChunk()) {
                final Vec2f chunk = new Vec2f(packet2.getChunkX() * 16, packet2.getChunkZ() * 16);
                if (!chunkDataList.contains(chunk)) {
                    chunkDataList.add(chunk);
                }
            }
        }
        return true;
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        List<Vec2f> found = new ArrayList<>();
        for (Vec2f chunkData : chunkDataList) {
            if (chunkData != null) {
                if (mc.player.getDistance(chunkData.x, mc.player.posY, chunkData.y) > MaxDistance.getValDouble())
                    found.add(chunkData);
                double renderPosX = chunkData.x - mc.getRenderManager().viewerPosX;
                double renderPosY = -mc.getRenderManager().viewerPosY;
                double renderPosZ = chunkData.y - mc.getRenderManager().viewerPosZ;
                final AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 16, renderPosY + 1.0, renderPosZ + 16);

                RenderBlock(Mode.getValString(), bb, OverlayColor.getcolor(), LineWidth.getValDouble());
            }
        }

        chunkDataList.removeAll(found);
        super.onRenderWorldLast(event);
    }


}
