package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Random;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.isMoving;

public class Timer extends Module {


    public Setting Speed = setmgr.add(new Setting("Speed", this, 2, .1, 5, false));
    public Setting OnMove = setmgr.add(new Setting("OnMove", this, true));
    public Setting mode = setmgr.add(new Setting("Timer Mode", this, "Vanilla", "Vanilla", "Even", "Odd", "Random", "PerSec"));
    public Setting RandomTiming = setmgr.add(new Setting("Time per sec", this, .5, 0, 5, false, mode, "PerSec", 3));

    TimerUtils timer = new TimerUtils();
    public boolean switcheraro = false;
    Random randomno = new Random();

    public Timer() {
        super("Timer", Keyboard.KEY_NONE, Category.PLAYER, "Timer");
    }

    @Override
    public void onDisable() {
        setTickLength(50);
    }

    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Vanilla")) {
            if (!OnMove.getValBoolean())
                setTickLength((float) (50 / Speed.getValDouble()));
            if (OnMove.getValBoolean() && isMoving(mc.player))
                setTickLength((float) (50 / Speed.getValDouble()));
        }
        if (mode.getValString().equalsIgnoreCase("Random")) {
            if (randomno.nextBoolean()) {
                if (!OnMove.getValBoolean())
                    setTickLength((float) (50 / Speed.getValDouble()));
                if (OnMove.getValBoolean() && isMoving(mc.player))
                    setTickLength((float) (50 / Speed.getValDouble()));
            } else
                setTickLength(50);
        } else if (mode.getValString().equalsIgnoreCase("PerSec")) {
            if (this.timer.isDelay((long) (RandomTiming.getValDouble() * 1000))) {
                switcheraro = !switcheraro;
                if (switcheraro) {
                    if (!OnMove.getValBoolean())
                        setTickLength((float) (50 / Speed.getValDouble()));
                    if (OnMove.getValBoolean() && isMoving(mc.player))
                        setTickLength((float) (50 / Speed.getValDouble()));
                } else
                    setTickLength(50);
                this.timer.setLastMS();
            }
        }
        if (mode.getValString().equalsIgnoreCase("Even")) {
            if (mc.player.ticksExisted % 2 == 0) {
                if (!OnMove.getValBoolean())
                    setTickLength((float) (50 / Speed.getValDouble()));
                if (OnMove.getValBoolean() && isMoving(mc.player))
                    setTickLength((float) (50 / Speed.getValDouble()));
            } else setTickLength(50);
        }
        if (mode.getValString().equalsIgnoreCase("Odd")) {
            if (!(mc.player.ticksExisted % 2 == 0)) {
                if (!OnMove.getValBoolean())
                    setTickLength((float) (50 / Speed.getValDouble()));
                if (OnMove.getValBoolean() && isMoving(mc.player))
                    setTickLength((float) (50 / Speed.getValDouble()));
            } else setTickLength(50);
        }

        if (OnMove.getValBoolean() && !isMoving(mc.player))
            setTickLength(50);
    }

    private void setTickLength(float tickLength) {
        mc.timer.tickLength = 1 * tickLength;
    }

}
