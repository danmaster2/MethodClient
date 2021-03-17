package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;


public class Reach extends Module {
    Setting Reach=setmgr.add(Reach = new Setting("Reach", this, 10, 0, 20, true));

    public Reach() {
        super("Reach", Keyboard.KEY_NONE, Category.PLAYER, "Reach");
    }

    @Override
    public void onEnable() {
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(Reach.getValDouble());
    }

    @Override
    public void onDisable() {
        mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(5);
    }

}
