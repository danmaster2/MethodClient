package Method.Client.module.render;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;


public class Fullbright extends Module {
    private float oldBrightness;

    public Fullbright() {
        super("Fullbright", Keyboard.KEY_NONE, Category.RENDER, "Makes the screen bright");
    }

    Setting mode = setmgr.add(new Setting("Mode", this, "Potion", "Gamma", "Potion"));


    @Override
    public void onEnable() {
        if (mode.getValString().equalsIgnoreCase("Gamma")) {
            oldBrightness = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 10F;
        }
        if (mode.getValString().equalsIgnoreCase("Potion")) {
            PotionEffect nv = new PotionEffect(Objects.requireNonNull(Potion.getPotionById(16)), 9999999, 3);
            mc.player.addPotionEffect(nv);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldBrightness;
        mc.player.removeActivePotionEffect(Potion.getPotionById(16));

        super.onDisable();
    }
}
