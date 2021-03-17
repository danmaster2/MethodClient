package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class SmallShield extends Module {


    public SmallShield() {
        super("SmallShield", Keyboard.KEY_NONE, Category.PLAYER, "SmallShield");
    }

    Setting MainHand = setmgr.add(new Setting("MainHand", this, 1, 0, 2, true));
    Setting OffHand = setmgr.add(new Setting("OffHand", this, 2, 0, 2, true));
    Setting armPitch = setmgr.add(new Setting("Arm Pitch", this, 0, 90, 360, true));
    Setting armYaw = setmgr.add(new Setting("Arm Yaw", this, 0, 220, 360, true));


    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;

        itemRenderer.equippedProgressOffHand = (float) (0.5F * OffHand.getValDouble());
        itemRenderer.equippedProgressMainHand = (float) (0.5F * MainHand.getValDouble());

        mc.player.renderArmPitch = (float) armPitch.getValDouble();
        mc.player.renderArmYaw = (float) armYaw.getValDouble();

    }


}
