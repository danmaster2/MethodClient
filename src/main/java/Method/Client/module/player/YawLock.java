package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.PitchYawHelper;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

import static Method.Client.Main.setmgr;

public class YawLock extends Module {


    public YawLock() {
        super("YawLock", Keyboard.KEY_NONE, Category.PLAYER, "YawLock");
    }

    float YawOld = 0;
    boolean overshot;

    Setting auto = setmgr.add( new Setting("auto", this, true));
    Setting slice = setmgr.add( new Setting("slice", this, 45, 0, 360, true));
    Setting yaw = setmgr.add( new Setting("yaw", this, 0, -360, 360, true));
    Setting Silent = setmgr.add( new Setting("Silent", this, true));
    Setting Gradual = setmgr.add( new Setting("Gradual", this, true));
    Setting Gradualamnt = setmgr.add( new Setting("Gradualamnt", this, .1, 0, 1, false));
    Setting Wiggle = setmgr.add( new Setting("Wiggle", this, true));
    Setting Wiggleamnt = setmgr.add( new Setting("Wiggleamnt", this, .1, 0, 1, false));
    
    @Override
    public void onEnable() {
        mc.mouseHelper = new PitchYawHelper();
        PitchYawHelper.Yaw = true;
        YawOld = mc.player.rotationYaw;
        overshot = false;
        if (yaw.getValDouble() > 90 || yaw.getValDouble() < -90) {
            ChatUtils.warning("Out of normal Range! Use Silent?");
        }
    }

    @Override
    public void onDisable() {
        PitchYawHelper.Yaw = false;
    }

    float NewYaw;

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        PitchYawHelper.Yaw = !Silent.getValBoolean();

        NewYaw = YawOld;
        if (Gradual.getValBoolean() && !overshot) {
            YawOld += (YawOld < yaw.getValDouble()) ? +Gradualamnt.getValDouble() : -Gradualamnt.getValDouble();
            if ((NewYaw > yaw.getValDouble() && YawOld < yaw.getValDouble()) || (NewYaw < yaw.getValDouble() && YawOld > yaw.getValDouble())) {
                YawOld = (float) yaw.getValDouble();
                overshot = true;
            }
        }
        if (overshot && yaw.getValDouble() != YawOld) {
            overshot = false;
        }
        if (Wiggle.getValBoolean())
            NewYaw += Wiggleamnt.getValDouble() * (Math.random() > .5 ? +Math.random() : -Math.random());
        if (!Silent.getValBoolean()) {
            if (slice.getValDouble() == 0) return;
            if (auto.getValBoolean()) {
                int angle = (int) (360 / slice.getValDouble());
                float yaw = mc.player.rotationYaw;
                yaw = Math.round(yaw / angle) * angle;
                mc.player.rotationYaw = yaw;
                if (mc.player.isRiding()) Objects.requireNonNull(mc.player.getRidingEntity()).rotationYaw = yaw;
            } else {
                mc.player.rotationYaw = NewYaw;
            }
        }
        mc.player.rotationYawHead = NewYaw;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT)
            if (Silent.getValBoolean()) {
                if (packet instanceof CPacketPlayer.Rotation) {
                    CPacketPlayer.Rotation p = (CPacketPlayer.Rotation) packet;
                    p.yaw = NewYaw;
                }
                if (packet instanceof CPacketPlayer.PositionRotation) {
                    CPacketPlayer.PositionRotation p = (CPacketPlayer.PositionRotation) packet;
                    p.yaw = NewYaw;
                }
            }
        return true;
    }

}
