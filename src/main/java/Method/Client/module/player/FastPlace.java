package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class FastPlace extends Module {

    Setting Delay = setmgr.add(new Setting("Delay", this, 0, 0, 20, false));
    Setting XP = setmgr.add(new Setting("XP Only", this, false));
    Setting Crystal = setmgr.add(new Setting("Crystal Only", this, false));

    public FastPlace() {
        super("FastPlace", Keyboard.KEY_NONE, Category.PLAYER, "Place Blocks Faster");
    }

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (XP.getValBoolean() && (mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle
                || mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle)) {
            mc.rightClickDelayTimer = (int) Delay.getValDouble();

        }
        if (Crystal.getValBoolean() && (mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal
                || mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal)) {
            mc.rightClickDelayTimer = (int) Delay.getValDouble();

        }
        if (!(XP.getValBoolean() && Crystal.getValBoolean()))
            mc.rightClickDelayTimer = (int) Delay.getValDouble();

    }

}
