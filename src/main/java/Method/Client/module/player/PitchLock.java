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

public class PitchLock extends Module {


    public PitchLock() {
        super("PitchLock", Keyboard.KEY_NONE, Category.PLAYER, "PitchLock");
    }

    float PitchOLD = 0;
    boolean overshot;
    Setting auto = setmgr.add(new Setting("Auto Snap", this, true));
    Setting slice = setmgr.add(new Setting("Auto Slice", this, 45, 0, 360, true, auto, 1));
    Setting pitch = setmgr.add(new Setting("pitch", this, 0, -180, 180, true));
    Setting Gradual = setmgr.add(new Setting("Gradual", this, true));
    Setting Gradualamnt = setmgr.add(new Setting("Gradualamnt", this, .1, 0, 1, false));
    Setting Wiggle = setmgr.add(new Setting("Wiggle", this, true));
    Setting Wiggleamnt = setmgr.add(new Setting("Wiggleamnt", this, .1, 0, 1, false, Wiggle, 5));
    Setting Silent = setmgr.add(new Setting("Silent", this, true));


    @Override
    public void onEnable() {
        mc.mouseHelper = new PitchYawHelper();
        PitchYawHelper.Pitch = true;
        PitchOLD = mc.player.rotationPitch;
        overshot = false;
        if (pitch.getValDouble() > 90 || pitch.getValDouble() < -90) {
            ChatUtils.warning("Out of normal Range! Use Silent?");
        }
    }

    @Override
    public void onDisable() {
        PitchYawHelper.Pitch = false;
    }

    float NewPitch;

    @Override
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        PitchYawHelper.Pitch = !Silent.getValBoolean();

        NewPitch = PitchOLD;
        if (Gradual.getValBoolean() && !overshot) {
            PitchOLD += (PitchOLD < pitch.getValDouble()) ? +Gradualamnt.getValDouble() : -Gradualamnt.getValDouble();
            if ((NewPitch > pitch.getValDouble() && PitchOLD < pitch.getValDouble()) || (NewPitch < pitch.getValDouble() && PitchOLD > pitch.getValDouble())) {
                PitchOLD = (float) pitch.getValDouble();
                overshot = true;
            }
        }
        if (overshot && pitch.getValDouble() != PitchOLD) {
            overshot = false;
        }
        if (!Gradual.getValBoolean())
            NewPitch = (float) pitch.getValDouble();
        if (Wiggle.getValBoolean())
            NewPitch += Wiggleamnt.getValDouble() * (Math.random() > .5 ? +Math.random() : -Math.random());
        if (!Silent.getValBoolean()) {
            if (slice.getValDouble() == 0) return;
            if (auto.getValBoolean()) {
                int angle = (int) (360 / slice.getValDouble());
                float yaw = mc.player.rotationPitch;
                yaw = Math.round(yaw / angle) * angle;
                mc.player.rotationPitch = yaw;
                if (mc.player.isRiding()) Objects.requireNonNull(mc.player.getRidingEntity()).rotationPitch = yaw;
            } else {
                mc.player.rotationPitch = NewPitch;
            }
        }
        mc.player.renderYawOffset = NewPitch;
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.OUT)
            if (Silent.getValBoolean()) {
                if (packet instanceof CPacketPlayer.Rotation) {
                    CPacketPlayer.Rotation p = (CPacketPlayer.Rotation) packet;
                    p.pitch = NewPitch;
                }
                if (packet instanceof CPacketPlayer.PositionRotation) {
                    CPacketPlayer.PositionRotation p = (CPacketPlayer.PositionRotation) packet;
                    p.pitch = NewPitch;
                }
            }
        return true;
    }

}
