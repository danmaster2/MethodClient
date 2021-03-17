package Method.Client.module.movement;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Spider extends Module {

    public Spider() {
        super("Spider", Keyboard.KEY_NONE, Category.MOVEMENT, "Climb Walls");
    }

    Setting mode = setmgr.add(new Setting("Spider mode", this, "Vanilla", "NCP", "DEV", "Root", "Vanilla"));
    Setting Speed = setmgr.add(new Setting("Speed", this, .2, 0, 1, false, mode, "Vanilla", 1));
    public boolean shouldjump = true;
    public float forcedYaw;
    public float forcedPitch;

    @Override
    public void onEnable() {
        this.shouldjump = true;
    }

    @Override
    public void onDisable() {
        this.shouldjump = true;
        mc.player.stepHeight = 0.5F;
    }


    //////////////////
    @Override
    public void onClientTick(ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("Vanilla"))
            if (!mc.player.isOnLadder() && mc.player.collidedHorizontally && mc.player.motionY < 0.2) {
                mc.player.motionY = Speed.getValDouble();
            }
        if (mode.getValString().equalsIgnoreCase("Root")) {
            if (mc.player.collidedHorizontally && mc.player.ticksExisted % 4 == 0) {
                mc.player.motionY = 0.25D;
            }
        }
        if (mode.getValString().equalsIgnoreCase("DEV")) {
            if (mc.player.collidedHorizontally) {
                if (mc.player.ticksExisted % 4 == 0)
                    mc.player.motionY = 0.5D;
                else
                    mc.player.motionY = -0.01D;
            }
        }
        super.onClientTick(event);
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mode.getValString().equalsIgnoreCase("NCP")) {
            if (packet instanceof CPacketKeepAlive) {
                final CPacketKeepAlive packet2 = (CPacketKeepAlive) packet;
                if (mc.player.collidedHorizontally) {
                    mc.player.motionX *= 0.0D;
                    mc.player.motionZ *= 0.0D;
                    if (this.shouldjump) {
                        mc.player.jump();
                        this.shouldjump = false;
                    }

                    if (mc.player.fallDistance > 0.0F) {
                        mc.player.setPosition(mc.player.posX, mc.player.posY + 0.08D, mc.player.posZ);
                        mc.gameSettings.keyBindJump.pressed = false;
                        mc.player.onGround = true;
                        mc.player.stepHeight = 2.0F;
                    } else {
                        mc.player.stepHeight = 0.5F;
                    }


                } else {
                    this.forcedYaw = mc.player.rotationYaw;
                    this.forcedPitch = mc.player.rotationPitch;
                    this.shouldjump = true;
                    mc.player.stepHeight = 0.5F;
                }

            }
            if (packet instanceof CPacketPlayer) {
                final CPacketPlayer packet2 = (CPacketPlayer) packet;

                packet2.onGround = true;
                packet2.yaw = this.forcedYaw;
                packet2.pitch = this.forcedPitch;
            }
        }
        return true;
    }
}
