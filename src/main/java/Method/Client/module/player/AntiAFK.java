package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class AntiAFK extends Module {
    boolean switcheraro;
    Setting spin = setmgr.add(new Setting("spin", this, false));
    Setting delay = setmgr.add(new Setting("delay", this, 1, 0, 60, false));
    Setting swing = setmgr.add(new Setting("swing", this, true));
    Setting walk = setmgr.add(new Setting("walk", this, false));
    TimerUtils timer = new TimerUtils();

    public AntiAFK() {
        super("AntiAFK", Keyboard.KEY_NONE, Category.PLAYER, "Preforms action to not be kicked!");
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.timer.isDelay((long) (delay.getValDouble() * 1000))) {
            switcheraro = !switcheraro;
            if (switcheraro) {
                if (spin.getValBoolean())
                    Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Rotation(Utils.random(-160, 160), Utils.random(-160, 160), true));
                if (swing.getValBoolean())
                    Wrapper.INSTANCE.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (walk.getValBoolean()) {
                    int c = Utils.random(0, 10);
                    if (c == 4)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                    if (c == 1)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
                    if (c == 2)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
                    if (c == 3)
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
                }
            } else {
                if (walk.getValBoolean()) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                }
            }
            this.timer.setLastMS();
        }
    }
}
