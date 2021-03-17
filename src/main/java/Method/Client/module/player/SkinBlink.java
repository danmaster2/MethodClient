package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Random;

import static Method.Client.Main.setmgr;

public class SkinBlink extends Module {
     Setting mode = setmgr.add(new Setting("Mode", this, "Flat", "HORIZONTAL", "VERTICAL", "RANDOM"));
     Setting slowness = setmgr.add(new Setting("slowness", this, 2, 1, 2, true));
    private static final EnumPlayerModelParts[] PARTS_HORIZONTAL;
    private static final EnumPlayerModelParts[] PARTS_VERTICAL;
    private Random r;
    private int len;

    public SkinBlink() {
        super("SkinBlink", Keyboard.KEY_NONE, Category.PLAYER, "SkinBlink");
    }

    @Override
    public void setup() {
        this.r = new Random();
        this.len = EnumPlayerModelParts.values().length;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        switch (mode.getValString()) {
            case "RANDOM":
                if (mc.player.ticksExisted % this.slowness.getValDouble() != 0) {
                    return;
                }

                mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[this.r.nextInt(this.len)]);
                break;
            case "VERTICAL":
            case "HORIZONTAL":
                int i = (int) ((mc.player.ticksExisted / slowness.getValDouble()) % (PARTS_HORIZONTAL.length * 2));
                if (i >= PARTS_HORIZONTAL.length) {
                    i -= PARTS_HORIZONTAL.length;
                    mc.gameSettings.setModelPartEnabled(
                            mode.getValString().equalsIgnoreCase("Vertical") ? PARTS_VERTICAL[i] : PARTS_HORIZONTAL[i], true);
                }

        }
    }

    static {
        PARTS_HORIZONTAL = new EnumPlayerModelParts[]{EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.HAT, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG, EnumPlayerModelParts.RIGHT_SLEEVE};
        PARTS_VERTICAL = new EnumPlayerModelParts[]{EnumPlayerModelParts.HAT, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.RIGHT_SLEEVE, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG};
    }

}
