package Method.Client.module.player;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Entity301;
import Method.Client.utils.Patcher.Events.SetOpaqueCubeEvent;
import Method.Client.utils.system.Connection;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.Utils.directionSpeed;

public class FreeCam extends Module {

    public Entity301 entity301 = null;

    public FreeCam() {
        super("FreeCam", Keyboard.KEY_NONE, Category.PLAYER, "FreeCam");
    }

    Setting ShowPlayer = setmgr.add(new Setting("ShowPlayer", this, true));
    Setting Speed = setmgr.add(new Setting("Speed", this, 1, 0, 3, false));
    Setting Tp = setmgr.add(new Setting("Tp", this, false));

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.IN) {
            if (packet instanceof SPacketPlayerPosLook)
                return false;
        }
        return !(side == Connection.Side.OUT && packet instanceof CPacketPlayer);
    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.world != null && !Tp.getValBoolean()) {
            this.entity301 = new Entity301(mc.world, mc.player.getGameProfile());
            this.entity301.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
            this.entity301.inventory = mc.player.inventory;
            this.entity301.rotationPitch = mc.player.rotationPitch;
            this.entity301.rotationYaw = mc.player.rotationYaw;
            this.entity301.rotationYawHead = mc.player.rotationYawHead;
            if (ShowPlayer.getValBoolean())
                mc.world.spawnEntity(this.entity301);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.entity301 != null && mc.world != null) {
            mc.player.setPosition(this.entity301.posX, this.entity301.posY, this.entity301.posZ);
            mc.player.rotationPitch = this.entity301.rotationPitch;
            mc.player.rotationYaw = this.entity301.rotationYaw;
            mc.player.rotationYawHead = this.entity301.rotationYawHead;
            mc.world.removeEntity(this.entity301);
            this.entity301 = null;
        }
        mc.player.noClip = false;
        super.onDisable();
    }

    @Override
    public void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.player.deathTime > 0 || mc.player.getHealth() <= 0) {
            this.toggle();
            return;
        }


        EntityPlayerSP player = mc.player;
        player.capabilities.isFlying = false;
        player.motionY = 0;
        player.motionZ = 0;
        player.motionX = 0;
        final double[] directionSpeedVanilla = directionSpeed(Speed.getValDouble());
        player.jumpMovementFactor = (float) Speed.getValDouble();
        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX += directionSpeedVanilla[0];
            mc.player.motionZ += directionSpeedVanilla[1];
        }

        if (mc.gameSettings.keyBindJump.isKeyDown())
            player.motionY += Speed.getValDouble();
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            player.motionY -= Speed.getValDouble();

    }

    @Override
    public void onLivingUpdate(LivingUpdateEvent event) {
        mc.player.motionY = 0;

        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.player.motionY += Speed.getValDouble();
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY -= Speed.getValDouble();

        mc.player.noClip = true;
        super.onLivingUpdate(event);
    }

}
