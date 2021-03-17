package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;


public class F3Spoof extends Module {

    public F3Spoof() {
        super("F3Spoof", Keyboard.KEY_NONE, Category.RENDER, "F3Spoof");
    }

    public Setting Coords = setmgr.add(new Setting("Coords", this, true));
    public Setting FPS = setmgr.add(new Setting("FPS", this, true));
    public Setting Direction = setmgr.add(new Setting("Direction", this, true));
    public Setting Biome = setmgr.add(new Setting("Biome", this, true));
    public Setting System = setmgr.add(new Setting("System", this, true));


    @Override
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo) {
            for (int i = 0; i < event.getLeft().size(); i++) {
                if (Coords.getValBoolean()) {
                    if (event.getLeft().get(i).contains("Looking"))
                        event.getLeft().set(i, "Looking at a block!");
                    if (event.getLeft().get(i).contains("XYZ"))
                        event.getLeft().set(i, "XYZ: Hidden!");
                    if (event.getLeft().get(i).contains("Block:"))
                        event.getLeft().set(i, "Block: Hidden!");
                    if (event.getLeft().get(i).contains("Chunk:"))
                        event.getLeft().set(i, "Chunk: Hidden!");
                }
                if (FPS.getValBoolean())
                    if (event.getLeft().get(i).contains("fps"))
                        event.getLeft().set(i, "fps: 0!");
                if (Direction.getValBoolean())
                    if (event.getLeft().get(i).contains("Facing:"))
                        event.getLeft().set(i, "Facing: Hidden!");
                if (Biome.getValBoolean())
                    if (event.getLeft().get(i).contains("Biome:"))
                        event.getLeft().set(i, "Biome: Hidden!");
                if (System.getValBoolean()) {
                    if (event.getRight().get(i).contains("Display:")) {
                        event.getRight().set(i, "Display: 15360x 8640 (MethodClient)");
                        continue;
                    }
                    if (event.getRight().get(i).contains("CPU:")) {
                        event.getRight().set(i, "CPU: 256x Apple PowerPC");
                        continue;
                    }
                    if (event.getRight().get(i).contains("NVIDIA") || event.getRight().get(i).contains("AMD") || event.getRight().get(i).contains("PCIe")) {
                        event.getRight().set(i, "HIDDEN!");
                    }
                }
            }
        }
    }


}

