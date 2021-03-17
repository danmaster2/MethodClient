package Method.Client.module.player;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.system.Connection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class God extends Module {

    public God() {
        super("God", Keyboard.KEY_NONE, Category.PLAYER, "Take no damage in certain situations");
    }

    Setting mode = setmgr.add(new Setting("God mode", this, "Vanilla", "Vanilla", "TickMode", "Riding"));
    Setting Footsteps = setmgr.add(new Setting("Footsteps", this, false, mode, "Riding", 1));
    private Entity riding;

    @Override
    public void onEnable() {
        if (mode.getValString().equalsIgnoreCase("Riding")) {

            if (mc.player.getRidingEntity() != null) {
                this.riding = mc.player.getRidingEntity();
                mc.player.dismountRidingEntity();
                mc.world.removeEntity(this.riding);
                mc.player.setPosition(mc.player.getPosition().getX(), mc.player.getPosition().getY() - 1, mc.player.getPosition().getZ());
            }
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValString().equalsIgnoreCase("Riding")) {
            if (this.riding != null) {
                mc.player.connection.sendPacket(new CPacketUseEntity(this.riding, EnumHand.MAIN_HAND));
            }
        }
        if (mode.getValString().equalsIgnoreCase("TickMode")) {
            mc.player.respawnPlayer();
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (mode.getValString().equalsIgnoreCase("Riding") && side == Connection.Side.OUT) {
            if (packet instanceof CPacketUseEntity) {
                final CPacketUseEntity packet2 = (CPacketUseEntity) packet;
                if (this.riding != null) {
                    final Entity entity = packet2.getEntityFromWorld(mc.world);
                    if (entity != null) {
                        this.riding.posX = entity.posX;
                        this.riding.posY = entity.posY;
                        this.riding.posZ = entity.posZ;
                        this.riding.rotationYaw = mc.player.rotationYaw;
                        Movepackets(mc);
                    }
                }
            }
            if (packet instanceof CPacketPlayer.Position || packet instanceof CPacketPlayer.PositionRotation) {
                return false;
            }
        }
        return !(packet instanceof CPacketConfirmTeleport) || !mode.getValString().equalsIgnoreCase("Vanilla");
    }

    private void Movepackets(Minecraft mc) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
        mc.player.connection.sendPacket(new CPacketInput(mc.player.movementInput.moveForward, mc.player.movementInput.moveStrafe, false, false));
        mc.player.connection.sendPacket(new CPacketVehicleMove(this.riding));
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (mode.getValString().equalsIgnoreCase("Riding")) {
            if (this.riding != null) {
                this.riding.posX = mc.player.posX;
                this.riding.posY = mc.player.posY + (this.Footsteps.getValBoolean() ? 0.3f : 0.0f);
                this.riding.posZ = mc.player.posZ;
                this.riding.rotationYaw = mc.player.rotationYaw;
                Movepackets(mc);
            }
        }
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mode.getValString().equalsIgnoreCase("TickMode")) {
            mc.player.setHealth(20.0f);
            mc.player.getFoodStats().setFoodLevel(20);
            mc.player.isDead = false;
            if (mc.currentScreen instanceof GuiGameOver)
                mc.displayGuiScreen(null);
        }
        if (mc.currentScreen instanceof GuiGameOver && mode.getValString().equalsIgnoreCase("Tickmode")) {
            try {
                mc.player.respawnPlayer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
